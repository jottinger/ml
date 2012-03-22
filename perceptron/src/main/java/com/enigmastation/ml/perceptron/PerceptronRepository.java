package com.enigmastation.ml.perceptron;


import com.enigmastation.ml.perceptron.impl.NodeCreation;

import java.util.List;

public interface PerceptronRepository {
    double getStrength(int from, int to, Layer layer);

    void setStrength(int from, int to, Layer layer, double strength);

    List<Integer> getAllHiddenIds(List<Object> corpus, List<Object> targets);

    /*
    * This could use a cache *so* bad... assuming it's called a whole lot for the same terms.
    */
    int getNodeId(Object token, Layer layer, NodeCreation creation);

    void generateHiddenNodes(List<Object> corpus, List<Object> targets);
}
