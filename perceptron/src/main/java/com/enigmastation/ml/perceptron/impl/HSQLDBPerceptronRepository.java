/*
 Copyright 2012 Joseph B. Ottinger

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.enigmastation.ml.perceptron.impl;

import com.enigmastation.ml.perceptron.PerceptronRepository;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a repository for the perceptron that does internal resource pooling,
 * and is customized for use with HSQLDB.
 * <p/>
 * This can be generified, I think. It can also be changed to use a datasource
 * provider from JNDI.
 */
public class HSQLDBPerceptronRepository implements PerceptronRepository {
    final static int DEFAULT_ID = -1;

    public void clear() {
        try (Connection conn = getConnection()) {
            conn.prepareStatement("drop table node").execute();
            conn.prepareStatement("drop table wordhidden").execute();
            conn.prepareStatement("drop table hiddenword").execute();
            buildTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    static {
        GenericObjectPool connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory("jdbc:hsqldb:file:perceptron", "SA", "");
        @SuppressWarnings("UnusedDeclaration")
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        PoolingDriver driver = new PoolingDriver();
        driver.registerPool("perceptron", connectionPool);
    }

    public HSQLDBPerceptronRepository() {
        buildTables();
    }

    private void buildTables() {
        PreparedStatement ps;
        try (Connection conn = getConnection()) {
            List<String> tables = new ArrayList<>();
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                tables.add(rs.getString(3).toLowerCase());
            }
            if (!tables.contains("node")) {
                conn.prepareStatement("create table node(id bigint identity, layer int, create_key longvarchar)")
                        .execute();
            }
            if (!tables.contains("wordhidden")) {
                conn.prepareStatement("create table " + Layer.HIDDEN.getStoreName()
                        + " (id bigint identity, fromid bigint, toid bigint, strength double)")
                        .execute();
            }
            if (!tables.contains("hiddenword")) {
                conn.prepareStatement("create table " + Layer.TO.getStoreName()
                        + " (id bigint identity, fromid bigint, toid bigint, strength double)")
                        .execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * This could use a cache *so* bad... assuming it's called a whole lot for the same terms.
    */
    @Override
    public int getNodeId(Object token, Layer layer, NodeCreation creation) {
        int id = DEFAULT_ID;
        PreparedStatement ps;
        ResultSet rs;
        try (Connection conn = getConnection()) {
            ps = conn.prepareStatement("select id from node where create_key=? and layer=?");
            ps.setString(1, token.toString());
            ps.setInt(2, layer.ordinal());
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                if (creation == NodeCreation.CREATE) {
                    rs.close();
                    ps.close();
                    id = createNode(token, layer);
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    private int createNode(Object token, Layer layer) {
        int id;
        PreparedStatement ps;
        ResultSet rs;
        try (Connection conn = getConnection()) {
            ps = conn.prepareStatement("insert into node (create_key, layer) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, token.toString());
            ps.setInt(2, layer.ordinal());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            id = rs.getInt(1);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public void generateHiddenNodes(List<Object> corpus, List<Object> targets) {
        StringBuilder sb = new StringBuilder();
        for (Object o : corpus) {
            sb.append(":").append(o.toString());
        }
        // we need to know if it's not there, because we need to create a data set if not
        int id = getNodeId(sb.toString(), Layer.HIDDEN, NodeCreation.NO_CREATE);
        if (id == DEFAULT_ID) { // not found!
            id = createNode(sb.toString(), Layer.HIDDEN);
            // now create connections!
            for (Object token : corpus) {
                setStrength(getNodeId(token, Layer.FROM, NodeCreation.CREATE),
                        id, Layer.HIDDEN, 1.0 / corpus.size());
            }
            for (Object token : targets) {
                setStrength(id, getNodeId(token, Layer.TO, NodeCreation.CREATE),
                        Layer.TO, 0.1);
            }
        }

    }

    Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:apache:commons:dbcp:perceptron");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getStrength(int from, int to, Layer layer) {
        double strength = layer.getStrength();
        PreparedStatement ps;
        ResultSet rs;

        try (Connection conn = getConnection()) {
            ps = conn.prepareStatement("select strength from " + layer.getStoreName() +
                    " where fromid=? and toid=?");
            ps.setInt(1, from);
            ps.setInt(2, to);
            rs = ps.executeQuery();
            if (rs.next()) {
                strength = rs.getDouble(1);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return strength;
    }

    @Override
    public List<Integer> getAllHiddenIds(List<Object> corpus, List<Object> targets) {
        List<Integer> hiddenIds = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;
        try (Connection conn = getConnection()) {
            ps = conn.prepareStatement("select toid from " + Layer.HIDDEN.getStoreName()
                    + " where fromid=?");
            for (Object c : corpus) {
                ps.setInt(1, getNodeId(c, Layer.FROM, NodeCreation.CREATE));
                rs = ps.executeQuery();
                if (rs.next()) {
                    hiddenIds.add(rs.getInt(1));
                }
                rs.close();
            }
            ps.close();

            ps = conn.prepareStatement("select fromid from " + Layer.TO.getStoreName()
                    + " where toid=?");
            for (Object c : corpus) {
                ps.setInt(1, getNodeId(c, Layer.TO, NodeCreation.CREATE));
                rs = ps.executeQuery();
                if (rs.next()) {
                    hiddenIds.add(rs.getInt(1));
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return hiddenIds;
    }

    @Override
    public void setStrength(int from, int to, Layer layer, double strength) {
        PreparedStatement ps;
        ResultSet rs;
        try (Connection conn = getConnection()) {
            ps = conn.prepareStatement("select id from " + layer.getStoreName()
                    + " where fromid=? and toid=?");
            ps.setInt(1, from);
            ps.setInt(2, to);
            rs = ps.executeQuery();
            if (rs.next()) {
                // update
                int id = rs.getInt(1);
                rs.close();
                ps.close();
                ps = conn.prepareStatement("update " + layer.getStoreName()
                        + " set strength=? where id=?");
                ps.setDouble(1, strength);
                ps.setInt(2, id);
                ps.executeUpdate();
            } else {
                rs.close();
                ps.close();
                // insert
                ps = conn.prepareStatement("insert into " + layer.getStoreName() +
                        " (fromid, toid, strength) " +
                        " values (?, ?, ?)");
                ps.setInt(1, from);
                ps.setInt(2, to);
                ps.setDouble(3, strength);
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
