package com.enigmastation.ml.perceptron.impl;

import com.enigmastation.ml.model.Layer;
import com.enigmastation.ml.util.SessionManager;
import com.google.common.collect.Lists;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static org.testng.Assert.assertEquals;

public class PerceptronTest {

    final long wWorld = 101, wRiver = 102, wBank = 103,
            uWorldBank = 201, uRiver = 202, uEarth = 203;
    final long[] outputs = {uWorldBank, uRiver, uEarth};

    @Test
    public void testGetStrengthForNonexistentNodes() throws Exception {
        PerceptronImpl perceptron = new PerceptronImpl();
        assertEquals(perceptron.getStrength(-2, -1, Layer.HIDDEN), Layer.HIDDEN.getStrength());
        assertEquals(perceptron.getStrength(-2, -1, Layer.OUTPUT), Layer.OUTPUT.getStrength());
    }

    @Test
    public void testHiddenNode() {
        PerceptronImpl perceptron = new PerceptronImpl();
        long[] inputs = {102, 101};
        perceptron.generateHiddenNode(inputs, outputs);
        for (long l : perceptron.getAllHiddenIds(new long[]{101, 103}, new long[]{203})) {
            System.out.println(l);
        }
        dumpDB();
    }

    @Test
    public void testSetLong() {
        PerceptronImpl perceptron = new PerceptronImpl();
        System.out.println(Arrays.toString(perceptron.sortedUniqueArray(new long[]{1, 4, 5, 9, 2, 2, 1, 4, 3})));
    }

    @Test
    public void testResults() {
        clearDB();
        long[] inputs = {wWorld, wBank};
        PerceptronImpl perceptron = new PerceptronImpl();
        perceptron.generateHiddenNode(inputs, outputs);
        System.out.println(perceptron.getResults(inputs, outputs));
    }

    @Test
    public void testSimpleTrain() {
        //clearDB();
        PerceptronImpl perceptron = new PerceptronImpl();
        System.out.println(perceptron.getResults(new long[]{wWorld, wBank}, outputs));
        perceptron.trainquery(new long[]{wWorld, wBank}, outputs, uWorldBank);
        System.out.println(perceptron.getResults(new long[]{wWorld, wBank}, outputs));
    }

    @Test
    public void testSimpleTrain2() {
        PerceptronImpl perceptron = new PerceptronImpl();
        for (int i = 0; i < 30; i++) {
            perceptron.trainquery(new long[]{wWorld, wBank}, outputs, uWorldBank);
            perceptron.trainquery(new long[]{wRiver, wBank}, outputs, uRiver);
            perceptron.trainquery(new long[]{wWorld}, outputs, uEarth);
        }
        System.out.println(perceptron.getResults(new long[]{wWorld, wBank}, outputs));
        System.out.println(perceptron.getResults(new long[]{wRiver, wBank}, outputs));
        System.out.println(perceptron.getResults(new long[]{wBank}, outputs));
    }

    @Test
    public void testXOR() {
        PerceptronImpl perceptron = new PerceptronImpl();
        clearDB();
        long ITRUE = perceptron.getNeuronIdFor("iTrue"),
                XTRUE = perceptron.getNeuronIdFor("xTrue"),
                IFALSE = perceptron.getNeuronIdFor("iFalse"),
                XFALSE = perceptron.getNeuronIdFor("xFalse");
        long OTRUE = perceptron.getNeuronIdFor("oTrue"),
                OFALSE = perceptron.getNeuronIdFor("oFalse");
        long[] outputs = {OTRUE, OFALSE};
        boolean displayed = false;
//        Session session = SessionManager.getSession();
//        Transaction tx = session.beginTransaction();
        for (int i = 0; i < 2; i++) {
            perceptron.trainquery(new long[]{ITRUE, XTRUE}, outputs, OFALSE);
            perceptron.trainquery(new long[]{ITRUE, XFALSE}, outputs, OTRUE);
            perceptron.trainquery(new long[]{IFALSE, XTRUE}, outputs, OTRUE);
        }
//        tx.commit();
//        session.close();
        //perceptron.generateHiddenNode(new long[]{IFALSE,XFALSE}, outputs);
        dumpDB();
        System.out.printf("false, false, false (%03d): %s%n", OFALSE,
                format(perceptron.getResults(new long[]{XFALSE, IFALSE}, outputs)));
        System.out.printf("true , true , false (%03d): %s%n", OFALSE,
                format(perceptron.getResults(new long[]{ITRUE, XTRUE}, outputs)));
        System.out.printf("true , false, true  (%03d): %s%n", OTRUE,
                format(perceptron.getResults(new long[]{ITRUE, XFALSE}, outputs)));
        System.out.printf("false, true , true  (%03d): %s%n", OTRUE,
                format(perceptron.getResults(new long[]{IFALSE, XTRUE}, outputs)));
    }

    private String format(Map<Long, Double> results) {
        List<Map.Entry<Long, Double>> resultSet = Lists.newArrayList(results.entrySet());
        resultSet.sort((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()));
        StringJoiner joiner = new StringJoiner(",");
        for (Map.Entry<Long, Double> o : resultSet) {
            joiner.add(o.getKey() + "=" + o.getValue());
        }
        return joiner.toString();
    }

    private void dumpDB() {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            Query query;
            query = session.createQuery("from Synapse s order by s.layer, s.input, s.output");
            for (Object o : query.list()) {
                System.out.println(o);
            }
            query = session.createQuery("from Neuron n");
            for (Object o : query.list()) {
                System.out.println(o);
            }
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    private void clearDB() {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            Query query = null;
            String[] entities = {"Synapse", "Neuron"};
            for (String e : entities) {
                session.createQuery("delete from " + e + " e").executeUpdate();
            }
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }
}