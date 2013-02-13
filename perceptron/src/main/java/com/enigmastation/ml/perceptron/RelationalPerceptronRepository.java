package com.enigmastation.ml.perceptron;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: jottinge
 * Date: 2/13/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public interface RelationalPerceptronRepository extends PerceptronRepository {
    Connection getConnection();
}
