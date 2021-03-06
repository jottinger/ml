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

import com.enigmastation.ml.perceptron.Perceptron;
import com.enigmastation.ml.perceptron.PerceptronRepository;
import com.enigmastation.ml.perceptron.PerceptronResult;
import com.enigmastation.ml.perceptron.PerceptronState;
import com.enigmastation.ml.perceptron.annotations.NeuralNetwork;
import com.enigmastation.ml.tokenizer.Tokenizer;
import com.enigmastation.ml.tokenizer.impl.SimpleTokenizer;

import java.util.*;
import java.util.stream.IntStream;

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
    public PerceptronState buildPerceptron(List<?> corpus, List<?> targets) {
        PerceptronState state = new PerceptronState(corpus, targets);
        repository.generateHiddenNodes(corpus, targets);
        state.setupNetwork(repository);
        return state;
    }

    public List<Double> feedForward(PerceptronState state) {
        IntStream.range(0, state.getWordIdsSize()).forEach(i->state.setAi(i, 1.0));

        for (int j = 0; j < state.getHiddenIdsSize(); j++) {
            double sum = 0.0;
            for (int i = 0; i < state.getWordIdsSize(); i++) {
                sum += state.getAi(i) * state.getWi(j, i);
            }
            state.setAh(j, Math.tanh(sum));
        }
        for (int k = 0; k < state.getTargetIdsSize(); k++) {
            double sum = 0.0;
            for (int j = 0; j < state.getHiddenIdsSize(); j++) {
                double v = state.getWo(k, j);
                sum += state.getAh(j) * v;
            }
            state.setAo(k, Math.tanh(sum));
        }
        return state.getAo();
    }

    void backPropagate(PerceptronState state, Object target) {
        backPropagate(state, target, 0.5);
    }

    public void backPropagate(PerceptronState state, Object target, double n) {
        // we need to set the output weights to create the delta
        double target_weights[] = new double[state.getTargetIdsSize()];
        target_weights[state.indexOfTarget(target)] = 1.0;

        // calculate errors for output
        double output_deltas[] = new double[state.getTargetIdsSize()];
        for (int k = 0; k < state.getTargetIdsSize(); k++) {
            double error = target_weights[k] - state.getAo(k);
            output_deltas[k] = dtanh(state.getAo(k)) * error;
        }

        // calculate errors for hidden layer
        double hidden_deltas[] = new double[state.getHiddenIdsSize()];
        for (int j = 0; j < state.getHiddenIdsSize(); j++) {
            // j is an index to an id...
            double error = 0.0;
            for (int k = 0; k < state.getTargetIdsSize(); k++) {
                error += output_deltas[k] * state.getWo(k, j);
            }
            hidden_deltas[j] = dtanh(state.getAh(j)) * error;
        }
        // update output weights
        for (int j = 0; j < state.getHiddenIdsSize(); j++) {
            for (int k = 0; k < state.getTargetIdsSize(); k++) {
                double change = output_deltas[k] * state.getAh(j);
                Map<Integer, Double> m = state.getWo(k);
                double v = m.get(state.getHiddenId(j));

                m.put(state.getHiddenId(j), v + n * change);
            }
        }
        // update input weights        
        for (int i = 0; i < state.getWordIdsSize(); i++) {
            for (int j = 0; j < state.getHiddenIdsSize(); j++) {
                double change = hidden_deltas[j] * state.getAi(i);
                Map<Integer, Double> m = state.getWi(j);
                double v = m.get(state.getWordId(i));

                m.put(state.getWordId(i), v + n * change);
            }
        }
    }

    @Override
    public List<?> getAllTargets() {
        return repository.getAllTargets();
    }

    @Override
    public void train(List<?> corpus, List<?> targets, Object selected) {
        PerceptronState state = buildPerceptron(corpus, targets);
        feedForward(state);
        backPropagate(state, selected);
        updateStrengths(state);
    }

    @Override
    public Queue<PerceptronResult> getResults(List<?> corpus, List<?> targets) {
        Queue<PerceptronResult> queue = new PriorityQueue<>();
        PerceptronState state = buildPerceptron(corpus, targets);
        List<Double> results = feedForward(state);
        for (int j = 0; j < state.getTargetIdsSize(); j++) {
            queue.add(new PerceptronResult(targets.get(j), results.get(j)));
        }
        return queue;
    }

    @Override
    public Queue<PerceptronResult> getResults(Object corpus, List<?> targets) {
        // ugh
        return getResults(tokenizer.tokenize(corpus.toString()), targets);
    }

    @Override
    public Queue<PerceptronResult> getResults(Object corpus, Object[] targets) {
        return getResults(corpus, Arrays.asList(targets));
    }

    @Override
    public Object getFirstResult(List<?> corpus, List<?> targets) {
        return getResults(corpus, targets).peek().getTarget();
    }

    @Override
    public Object getFirstResult(Object corpus, List<?> targets) {
        return getFirstResult(tokenizer.tokenize(corpus.toString()), targets);
    }

    @Override
    public Object getFirstResult(Object corpus, Object[] targets) {
        return getFirstResult(corpus, Arrays.asList(targets));
    }

    @Override
    public Object getFirstResult(Object corpus) {
        return getFirstResult(corpus, getAllTargets());
    }

    @Override
    public Queue<PerceptronResult> getResults(Object corpus) {
        return getResults(corpus, getAllTargets());
    }

    @Override
    public void train(Object corpus, List<?> targets, Object selected) {
        train(tokenizer.tokenize(corpus.toString()), targets, selected);
    }

    @Override
    public void train(Object corpus, Object selected) {
        train(corpus, getAllTargets(), selected);
    }

    @Override
    public void createTarget(Object target) {
        repository.getNodeId(target, PerceptronRepository.Layer.TO, PerceptronRepository.NodeCreation.CREATE);
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
        for (Integer i : state.getWordIds()) {
            for (Integer j : state.getHiddenIds()) {
                repository.setStrength(i, j, PerceptronRepository.Layer.HIDDEN, state.getWiById(j, i));
            }
        }
        for (Integer j : state.getHiddenIds()) {
            for (Integer k : state.getTargetIds()) {
                repository.setStrength(j, k, PerceptronRepository.Layer.TO, state.getWoById(k, j));
            }
        }
    }
}