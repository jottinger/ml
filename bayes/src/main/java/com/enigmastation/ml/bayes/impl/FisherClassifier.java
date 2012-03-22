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

package com.enigmastation.ml.bayes.impl;

import com.enigmastation.ml.bayes.ClassifierDataFactory;
import com.enigmastation.ml.bayes.annotations.BayesClassifier;
import com.enigmastation.ml.bayes.annotations.FisherBayesClassifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BayesClassifier
@FisherBayesClassifier
public class FisherClassifier extends SimpleClassifier {
    Map<Object, Double> minimums = new HashMap<>();

    public double getMinimum(Object category) {
        if (minimums.containsKey(category)) {
            return minimums.get(category);
        } else {
            return 0.0;
        }
    }

    public void setMinimum(Object category, double v) {
        minimums.put(category, v);
    }

    public FisherClassifier() {
    }

    public FisherClassifier(ClassifierDataFactory factory) {
        super(factory);
    }

    double featureProb(Object feature, Object category) {
        double clf = super.featureProb(feature, category);
        if (clf == 0.0) {
            return 0.0;
        }
        double frequencySum = 0.0;
        for (Object c : categories()) {
            frequencySum += super.featureProb(feature, c);
        }
        return clf / frequencySum;
    }

    double fisherProbability(Object source, Object category) {
        double p = 1.0;
        List<Object> features = getFeatures(source);
        for (Object f : features) {
            p *= weightedProb(f, category);
        }
        double fisherScore = -2.0 * Math.log(p);
        return invChi(fisherScore, features.size() * 2);
    }

    protected double invChi(double chi, double df) {
        double m = chi / 2.0;
        double sum = Math.exp(-m);
        double term = sum;
        for (int i = 1; i < df / 2; i++) {
            term *= (m / i);
            sum += term;
        }
        return Math.min(sum, 1.0);
    }

    @Override
    public Object classify(Object source, Object defaultClassification) {
        Object best = defaultClassification;
        double max = 0.0;
        for (Object c : categories()) {
            double p = fisherProbability(source, c);
            if (p > getMinimum(c) && p > max) {
                best = c;
                max = p;
            }
        }
        return best;
    }
}
