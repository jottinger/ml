package com.enigmastation.ml.perceptron;

import com.enigmastation.ml.perceptron.impl.PerceptronState;

import java.util.List;
import java.util.Queue;

public interface Perceptron {
    PerceptronState buildPerceptron(List<Object> corpus, List<Object> targets);

    List<Double> feedForward(PerceptronState state);

    void backPropagate(PerceptronState state, Object target, double n);

    void train(List<Object> corpus, List<Object> targets, Object selected);

    Queue<PerceptronResult> getResult(List<Object> corpus, List<Object> targets);
}
