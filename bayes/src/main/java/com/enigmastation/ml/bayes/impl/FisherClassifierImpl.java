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

import com.enigmastation.ml.bayes.FisherClassifier;
import com.enigmastation.ml.bayes.annotations.BayesClassifier;
import com.enigmastation.ml.bayes.annotations.FisherBayesClassifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * This is a bayesian classifier that uses the Fisher method. See https://en.wikipedia.org/wiki/Fisher_information
 * for more.
 */
@BayesClassifier
@FisherBayesClassifier
public class FisherClassifierImpl extends SimpleClassifierImpl implements FisherClassifier {
    private Map<Serializable, Double> minimums = new ConcurrentHashMap<>();

    /**
     * This accesses the current minimum strength for the category. If the probability of a
     * classification operation is less than this strength for this category, the result is discarded.
     *
     * @param category The category for which to acquire the minimum strength
     * @return the strength
     */
    @Override
    public double getMinimum(Serializable category) {
        return minimums.computeIfAbsent(category, f -> 0.0);
    }

    /**
     * This sets the minimum strength required for the supplied category to be considered as a
     * classification result. If the classification operation yields a weaker probability than
     * this strength, the result is ignored.
     * <p>
     * The result of this is that you are able to say "classify as category 'x' but only if 'x'
     * is very likely," for example.
     *
     * @param category the category for which the strength is mutated
     * @param strength the minimum probability to accept for this category
     */
    @Override
    public void setMinimum(Serializable category, double strength) {
        minimums.put(category, strength);
    }

    @Override
    protected double featureProb(Serializable feature, Serializable category) {
        double clf = super.featureProb(feature, category);

        if (Math.abs(clf)<0.000001) {
            return 0.0;
        }

        double frequencySum = categories
                .keySet()
                .stream()
                .mapToDouble(c -> super.featureProb(feature, c))
                .sum();
        return clf / frequencySum;
    }

    double fisherProbability(Serializable source, Serializable category) {
        final double[] p = {1.0};
        List<Serializable> features = getFeatures(source);
        features
                .stream()
                .mapToDouble(f -> weightedProb(f, category))
                .forEach(w -> p[0] *= w);
        double fisherScore = -2.0 * Math.log(p[0]);
        return invChi(fisherScore, features.size() * 2.0);
    }

    private double invChi(double chi, double df) {
        double m = chi / 2.0;
        final double[] sum = {Math.exp(-m)};
        final double[] term = {sum[0]};
        IntStream.range(1, (int) (df / 2))
                .forEach(i -> {
                    term[0] *= m / i;
                    sum[0] += term[0];
                });
        return Math.min(sum[0], 1.0);
    }

    /**
     * This returns the best-match classification from the bayesian engine if
     * and only if the classification is more probable than the default thresholds
     * for classification.
     *
     * @param source the source corpus for the classification operation
     * @return the best-match classification
     */
    @Override
    public Serializable classify(Serializable source, Serializable defaultClassification) {
        Serializable[] best = {defaultClassification};
        double[] max = {0.0};

        getCategories().forEach(c -> {
            double p = fisherProbability(source, c);
            if (p > getMinimum(c) && p > max[0]) {
                best[0] = c;
                max[0] = p;
            }
        });
        return best[0];
    }
}
