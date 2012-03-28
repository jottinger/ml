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

package com.enigmastation.ml.perceptron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a class that represents a specific classification/training phase.
 * <p/>
 * It is <strong>NOT</strong> meant to be reused. Train, then discard.
 */
public final class PerceptronState {
    private final List<?> corpus;
    private final List<?> targets;
    private final List<Integer> wordIds = new ArrayList<>();
    private final List<Integer> targetIds = new ArrayList<>();
    private final List<Integer> hiddenIds = new ArrayList<>();
    private final List<Double> ai = new ArrayList<>();
    private final List<Double> ah = new ArrayList<>();
    private final List<Double> ao = new ArrayList<>();

    private final Map<Integer, Map<Integer, Double>> wi = new HashMap<>();
    private final Map<Integer, Map<Integer, Double>> wo = new HashMap<>();

    public PerceptronState(List<?> corpus, List<?> targets) {
        this.corpus = corpus;
        this.targets = targets;
    }

    public void setupNetwork(PerceptronRepository repository) {
        for (Object o : corpus) {
            wordIds.add(repository.getNodeId(o, PerceptronRepository.Layer.FROM, PerceptronRepository.NodeCreation.CREATE));
        }
        for (Object o : targets) {
            targetIds.add(repository.getNodeId(o, PerceptronRepository.Layer.TO, PerceptronRepository.NodeCreation.CREATE));
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
                targetMap.put(c, repository.getStrength(c, h, PerceptronRepository.Layer.HIDDEN));
            }
        }
        for (int t : targetIds) {
            Map<Integer, Double> targetMap = new HashMap<>();
            wo.put(t, targetMap);
            for (int h : hiddenIds) {
                targetMap.put(h, repository.getStrength(h, t, PerceptronRepository.Layer.TO));
            }
        }
    }

    public void setAi(int index, double value) {
        ai.set(index, value);
    }

    public int getWordIdsSize() {
        return wordIds.size();
    }

    public int getHiddenIdsSize() {
        return hiddenIds.size();
    }

    public double getAi(int index) {
        return ai.get(index);
    }

    public int getHiddenId(int index) {
        return hiddenIds.get(index);
    }

    public int getWordId(int index) {
        return wordIds.get(index);
    }

    public double getWi(int j, int i) {
        return wi.get(getHiddenId(j)).get(getWordId(i));
    }

    public void setAh(int index, double value) {
        ah.set(index, value);
    }

    public int getTargetIdsSize() {
        return targetIds.size();
    }

    public double getWo(int k, int j) {
        return wo.get(getTargetId(k)).get(getHiddenId(j));
    }

    private int getTargetId(int index) {
        return targetIds.get(index);
    }

    public double getAh(int index) {
        return ah.get(index);
    }

    public void setAo(int index, double value) {
        ao.set(index, value);
    }

    public List<Double> getAo() {
        return ao;
    }

    public int indexOfTarget(Object target) {
        return targets.indexOf(target);
    }

    public double getAo(int index) {
        return ao.get(index);
    }

    public Map<Integer, Double> getWo(int k) {
        return wo.get(getTargetId(k));
    }

    public Map<Integer, Double> getWi(int j) {
        return wi.get(getHiddenId(j));
    }

    public List<Integer> getWordIds() {
        return wordIds;
    }

    public List<Integer> getHiddenIds() {
        return hiddenIds;
    }

    public List<Integer> getTargetIds() {
        return targetIds;
    }

    public double getWiById(Integer j, Integer i) {
        return wi.get(j).get(i);
    }

    public double getWoById(Integer k, Integer j) {
        return wo.get(k).get(j);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PerceptronState");
        sb.append("{corpus=").append(corpus);
        sb.append(",\n targets=").append(targets);
        sb.append(",\n wordIds=").append(wordIds);
        sb.append(",\n targetIds=").append(targetIds);
        sb.append(",\n hiddenIds=").append(hiddenIds);
        sb.append(",\n ai=").append(ai);
        sb.append(",\n ah=").append(ah);
        sb.append(",\n ao=").append(ao);
        sb.append(",\n wi=").append(wi);
        sb.append(",\n wo=").append(wo);
        sb.append('}');
        return sb.toString();
    }
}
