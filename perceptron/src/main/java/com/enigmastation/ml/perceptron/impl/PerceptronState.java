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

import com.enigmastation.ml.perceptron.Layer;
import com.enigmastation.ml.perceptron.PerceptronRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerceptronState {
    final List<Object> corpus;
    final List<Object> targets;
    final List<Integer> wordIds = new ArrayList<>();
    final List<Integer> targetIds = new ArrayList<>();
    final List<Integer> hiddenIds = new ArrayList<>();
    final List<Double> ai = new ArrayList<>();
    final List<Double> ah = new ArrayList<>();
    final List<Double> ao = new ArrayList<>();

    final Map<Integer, Map<Integer, Double>> wi = new HashMap<>();
    final Map<Integer, Map<Integer, Double>> wo = new HashMap<>();

    public PerceptronState(List<Object> corpus, List<Object> targets) {
        this.corpus = corpus;
        this.targets = targets;
    }

    public void setupNetwork(PerceptronRepository repository) {
        for (Object o : corpus) {
            wordIds.add(repository.getNodeId(o, Layer.FROM, NodeCreation.CREATE));
        }
        for (Object o : targets) {
            targetIds.add(repository.getNodeId(o, Layer.TO, NodeCreation.CREATE));
        }
        hiddenIds.addAll(repository.getAllHiddenIds(corpus, targets));
        for (int i : wordIds) {
            ai.add(1.0);
        }
        for (int i : targetIds) {
            ao.add(1.0);
        }
        for (int i : hiddenIds) {
            ah.add(1.0);
        }
        for (int h : hiddenIds) {
            Map<Integer, Double> targetMap = new HashMap<>();
            wi.put(h, targetMap);
            for (int c : wordIds) {
                targetMap.put(c, repository.getStrength(c, h, Layer.HIDDEN));
            }
        }
        for (int t : targetIds) {
            Map<Integer, Double> targetMap = new HashMap<>();
            wo.put(t, targetMap);
            for (int h : hiddenIds) {
                targetMap.put(h, repository.getStrength(h, t, Layer.TO));
            }
        }
    }
}
