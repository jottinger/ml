package com.enigmastation.ml.perceptron.impl;

import com.enigmastation.ml.perceptron.Layer;
import com.enigmastation.ml.perceptron.Perceptron;
import com.enigmastation.ml.perceptron.PerceptronRepository;
import com.enigmastation.ml.perceptron.PerceptronResult;
import com.enigmastation.ml.perceptron.annotations.NeuralNetwork;
import com.enigmastation.ml.tokenizer.Tokenizer;
import com.enigmastation.ml.tokenizer.impl.SimpleTokenizer;

import java.util.*;

@com.enigmastation.ml.perceptron.annotations.Perceptron
@NeuralNetwork
public class PerceptronImpl implements Perceptron {
    PerceptronRepository repository;
    Tokenizer tokenizer = new SimpleTokenizer();

    private double dtanh(double y) {
        return 1.0 - y * y;
    }

    public PerceptronImpl(PerceptronRepository repository) {
        this.repository = repository;
    }

    @Override
    public PerceptronState buildPerceptron(List<Object> corpus, List<Object> targets) {
        PerceptronState state = new PerceptronState(corpus, targets);
        repository.generateHiddenNodes(corpus, targets);
        state.setupNetwork(repository);
        return state;
    }

    @Override
    public List<Double> feedForward(PerceptronState state) {
        for (int i = 0; i < state.wordIds.size(); i++) {
            state.ai.set(i, 1.0);
        }
        for (int j = 0; j < state.hiddenIds.size(); j++) {
            double sum = 0.0;
            for (int i = 0; i < state.wordIds.size(); i++) {
                sum += state.ai.get(i) * state.wi.get(state.hiddenIds.get(j)).get(state.wordIds.get(i));
            }
            state.ah.set(j, Math.tanh(sum));
        }
        for (int k = 0; k < state.targetIds.size(); k++) {
            double sum = 0.0;
            for (int j = 0; j < state.hiddenIds.size(); j++) {
                double v = state.wo.get(state.targetIds.get(k)).get(state.hiddenIds.get(j));
                sum += state.ah.get(j) * v;
            }
            state.ao.set(k, Math.tanh(sum));
        }
        return state.ao;
    }

    void backPropagate(PerceptronState state, Object target) {
        backPropagate(state, target, 0.5);
    }

    @Override
    public void backPropagate(PerceptronState state, Object target, double n) {
        // we need to set the output weights to create the delta
        double target_weights[] = new double[state.targets.size()];
        target_weights[state.targets.indexOf(target)] = 1.0;

        // calculate errors for output
        double output_deltas[] = new double[state.targets.size()];
        for (int k = 0; k < state.targets.size(); k++) {
            double error = target_weights[k] - state.ao.get(k);
            output_deltas[k] = dtanh(state.ao.get(k)) * error;
        }

        // calculate errors for hidden layer
        double hidden_deltas[] = new double[state.hiddenIds.size()];
        for (int j = 0; j < state.hiddenIds.size(); j++) {
            // j is an index to an id...
            double error = 0.0;
            for (int k = 0; k < state.targetIds.size(); k++) {
                error += output_deltas[k] * state.wo.get(state.targetIds.get(k)).get(state.hiddenIds.get(j));
            }
            hidden_deltas[j] = dtanh(state.ah.get(j)) * error;
        }
        // update output weights
        for (int j = 0; j < state.hiddenIds.size(); j++) {
            for (int k = 0; k < state.targetIds.size(); k++) {
                double change = output_deltas[k] * state.ah.get(j);
                Map<Integer, Double> m = state.wo.get(state.targetIds.get(k));
                double v = m.get(state.hiddenIds.get(j));

                m.put(state.hiddenIds.get(j), v + n * change);
            }
        }
        // update input weights        
        for (int i = 0; i < state.wordIds.size(); i++) {
            for (int j = 0; j < state.hiddenIds.size(); j++) {
                double change = hidden_deltas[j] * state.ai.get(i);
                Map<Integer, Double> m = state.wi.get(state.hiddenIds.get(j));
                double v = m.get(state.wordIds.get(i));

                m.put(state.wordIds.get(i), v + n * change);
            }
        }
    }

    @Override
    public void train(List<Object> corpus, List<Object> targets, Object selected) {
        PerceptronState state = buildPerceptron(corpus, targets);
        feedForward(state);
        backPropagate(state, selected);
        updateStrengths(state);
    }

    @Override
    public Queue<PerceptronResult> getResults(List<Object> corpus, List<Object> targets) {
        Queue<PerceptronResult> queue = new PriorityQueue<>();
        PerceptronState state = buildPerceptron(corpus, targets);
        List<Double> results = feedForward(state);
        for (int j = 0; j < state.targetIds.size(); j++) {
            queue.add(new PerceptronResult(targets.get(j), results.get(j)));
        }
        return queue;
    }

    @Override
    public Queue<PerceptronResult> getResults(Object corpus, List<Object> targets) {
        return getResults(tokenizer.tokenize(corpus), targets);
    }

    @Override
    public Queue<PerceptronResult> getResults(Object corpus, Object[] targets) {
        return getResults(corpus, Arrays.asList(targets));
    }

    @Override
    public Object getFirstResult(List<Object> corpus, List<Object> targets) {
        return getResults(corpus, targets).peek().getTarget();
    }

    @Override
    public Object getFirstResult(Object corpus, List<Object> targets) {
        return getFirstResult(tokenizer.tokenize(corpus), targets);
    }

    @Override
    public Object getFirstResult(Object corpus, Object[] targets) {
        return getFirstResult(corpus, Arrays.asList(targets));
    }

    @Override
    public void train(Object corpus, List<Object> targets, Object selected) {
        train(tokenizer.tokenize(corpus), targets, selected);
    }

    @Override
    public void train(Object corpus, Object[] targets, Object selected) {
        train(corpus, Arrays.asList(targets), selected);
    }

    @Override
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    @Override
    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private void updateStrengths(PerceptronState state) {
        for (Integer i : state.wordIds) {
            for (Integer j : state.hiddenIds) {
                repository.setStrength(i, j, Layer.HIDDEN, state.wi.get(j).get(i));
            }
        }
        for (Integer j : state.hiddenIds) {
            for (Integer k : state.targetIds) {
                repository.setStrength(j, k, Layer.TO, state.wo.get(k).get(j));
            }
        }
    }
}
