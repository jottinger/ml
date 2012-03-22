package com.enigmastation.ml.perceptron;

public enum Layer {
    FROM,
    HIDDEN("wordhidden", -0.2),
    TO("hiddenword", 0.0);
    String tableName;
    double strength = 0.0;

    Layer() {
    }

    Layer(String tableName, double strength) {
        this.tableName = tableName;
        this.strength = strength;
    }

    public double getStrength() {
        return strength;
    }

    public String getTableName() {
        if (tableName == null) {
            throw new IllegalArgumentException("tableName not implemented for " + this);
        }
        return tableName;
    }
}
