package com.enigmastation.ml.perceptron;

import java.util.Map;

public interface Perceptron {
    default double dtanh(double y) {
        return 1.0 - y * y;
    }

    Map<Long, Double> getResults(long[] wordids, long[] urlids);

    void trainquery(long[] inputs, long[] outputs, long target);

    long getNeuronIdFor(String s);
}
