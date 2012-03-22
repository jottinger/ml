package com.enigmastation.ml.perceptron;

import com.enigmastation.ml.perceptron.impl.PerceptronState;
import com.enigmastation.ml.tokenizer.Tokenizer;

import java.util.List;
import java.util.Queue;

public interface Perceptron {
    PerceptronState buildPerceptron(List<Object> corpus, List<Object> targets);

    List<Double> feedForward(PerceptronState state);

    void backPropagate(PerceptronState state, Object target, double n);

    void train(List<Object> corpus, List<Object> targets, Object selected);

    Queue<PerceptronResult> getResults(List<Object> corpus, List<Object> targets);

    void train(Object corpus, List<Object> targets, Object selected);

    void train(Object corpus, Object[] targets, Object selected);

    Tokenizer getTokenizer();

    void setTokenizer(Tokenizer tokenizer);

    Queue<PerceptronResult> getResults(Object corpus, List<Object> targets);

    Queue<PerceptronResult> getResults(Object corpus, Object[] targets);

    Object getFirstResult(List<Object> corpus, List<Object> targets);

    Object getFirstResult(Object corpus, List<Object> targets);

    Object getFirstResult(Object corpus, Object[] targets);
}
