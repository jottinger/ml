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

import com.enigmastation.ml.bayes.Feature;
import com.enigmastation.ml.bayes.SimpleClassifier;
import com.enigmastation.ml.bayes.annotations.BayesClassifier;
import com.enigmastation.ml.bayes.annotations.NaiveBayesClassifier;
import com.enigmastation.ml.tokenizer.Tokenizer;
import com.enigmastation.ml.tokenizer.impl.PorterTokenizer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a simple (naive) bayesian classifier.
 */
@BayesClassifier
@NaiveBayesClassifier
public class SimpleClassifierImpl implements SimpleClassifier {
    private static final ThreadLocal<Serializable> lastData = new ThreadLocal<>();
    private static final ThreadLocal<List<Serializable>> lastFeatures = new ThreadLocal<>();
    private Map<Serializable, Feature> features = new ConcurrentHashMap<>();
    Map<Serializable, Integer> categories = new ConcurrentHashMap<>();
    private Tokenizer tokenizer = new PorterTokenizer();
    private Map<Serializable, Double> thresholds = new ConcurrentHashMap<>();

    /**
     * This returns the best-match classification from the bayesian engine.
     * The default classification is "none", which will be returned if
     * no other classification matches.
     *
     * @param source the source corpus for the classification operation
     * @return the best-match classification
     */
    @Override
    public Serializable classify(Serializable source) {
        return classify(source, "none");
    }

    /**
     * This returns the best-match classification from the bayesian engine if
     * and only if the classification is more probable than the default threshold
     * for classification.
     * <p>
     * The default threshold is normally 0.0, which means the default classification
     * will be used only if no match at all is found with this method.
     * <p>
     * As a result, it probably shouldn't be used. You should prefer
     * classify(Object, Object, double) instead.
     *
     * @param source the source corpus for the classification operation
     * @return the best-match classification
     */
    @Override
    public Serializable classify(Serializable source, Serializable defaultClassification) {
        return classify(source, defaultClassification, 0.0);
    }

    /**
     * This returns the best-match classification from the bayesian engine if
     * and only if the classification is more probable than the strength threshold.
     * <p>
     * If the best-match classification is less probable than the threshold,
     * the default classification is returned.
     *
     * @param source                the source corpus for the classification operation
     * @param defaultClassification the default classification if the best-match
     *                              is less strong than strength
     * @param strength              the strength threshold for the best-match
     * @return the best-match or default classification
     */
    @Override
    public Serializable classify(Serializable source, Serializable defaultClassification, double strength) {
        Map<Serializable, Double> probabilities = getClassificationProbabilities(source);
        double max = 0.0;
        Serializable category = null;

        for (Map.Entry<Serializable, Double> entry : probabilities.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                category = entry.getKey();
            }
        }
        for (Map.Entry<Serializable, Double> entry : probabilities.entrySet()) {
            if (entry.getKey().equals(category)) {
                continue;
            }

            if ((entry.getValue() * getThreshold(category)) > probabilities.get(category)) {
                return defaultClassification;
            }
        }
        return category;
    }

    /**
     * This method returns the raw classification data, as a map. The map contains
     * the classification as a key, which maps to the classification's strength.
     *
     * @param source the source corpus for the classification operation
     * @return A Map where the key is the classification category and the value
     * is the strength of that category
     */
    @Override
    public Map<Serializable, Double> getClassificationProbabilities(Serializable source) {
        Map<Serializable, Double> probabilities = new HashMap<>();
        for (Serializable category : getCategories()) {
            probabilities.put(category, documentProbability(source, category));
        }
        return probabilities;
    }

    /**
     * This method trains the classifier.
     *
     * @param source         The source text for the training operation
     * @param classification The classification for which to train
     */
    @Override
    public void train(Serializable source, Serializable classification) {
        List<Serializable> features = getFeatures(source);
        features.forEach(f -> incrementFeature(f, classification));
        incrementCategory(classification);
    }

    /**
     * This method returns the tokenized features for a given source object.
     * It caches the data in a ThreadLocal, which may yield performance enhancements.
     *
     * @param source The source to tokenize
     * @return The tokenized source
     */
    List<Serializable> getFeatures(Serializable source) {
        List<Serializable> features;
        if (source.equals(lastData.get())) {
            features = lastFeatures.get();
        } else {
            features = tokenizer.tokenize(source);
            lastFeatures.set(features);
        }
        lastData.set(source);
        return features;
    }

    private void incrementFeature(Serializable feature, Serializable category) {
        Feature f = features.computeIfAbsent(feature, Feature::new);
        features.put(feature, f);
        f.incrementCategoryCount(category);
    }

    private void incrementCategory(Serializable category) {
        categories.put(category, categories.getOrDefault(category, 0) + 1);
    }

    // the number of times a feature has occurred in a category
    int featureCount(Serializable feature, Serializable category) {
        Feature f = features.get(feature);
        if (f == null) {
            return 0;
        }
        return f.getCountForCategory(category);
    }

    private int categoryCount(Serializable category) {
        if (categories.containsKey(category)) {
            return categories.get(category);
        }
        return 0;
    }

    private int totalCount() {
        int sum = 0;
        for (Integer i : categories.values()) {
            sum += i;
        }
        return sum;
    }

    Set<Serializable> getCategories() {
        return categories.keySet();
    }

    double featureProb(Serializable feature, Serializable category) {
        if (categoryCount(category) == 0) {
            return 0.0;
        }
        return (1.0 * featureCount(feature, category)) / categoryCount(category);
    }

    private double weightedProb(Serializable feature, Serializable category, double weight, double assumedProbability) {
        double basicProbability = featureProb(feature, category);

        double totals = 0;
        for (Serializable cat : getCategories()) {
            totals += featureCount(feature, cat);
        }
        return ((weight * assumedProbability) + (totals * basicProbability)) / (weight + totals);
    }

    private double weightedProb(Serializable feature, Serializable category, double weight) {
        return weightedProb(feature, category, weight, 0.5);
    }

    double weightedProb(Serializable feature, Serializable category) {
        return weightedProb(feature, category, 1.0);
    }

    /* naive bayes, very naive - and not what we usually need. */
    private double documentProbability(Serializable source, Serializable category) {
        List<Serializable> documentProbabilityFeatures = getFeatures(source);
        double p = 1.0;
        for (Serializable f : documentProbabilityFeatures) {
            p *= weightedProb(f, category);
        }
        return p;
    }

    double prob(Serializable corpus, Serializable category) {
        double categoryProbability = (1.0 * categoryCount(category)) / totalCount();
        double documentProbability = documentProbability(corpus, category);
        return documentProbability * categoryProbability;
    }

    /**
     * This sets the minimum threshold for a given category. If the classification operation
     * yields less probability than this threshold, the result is discarded.
     *
     * @param category  The category for which to set the threshold
     * @param threshold the minimum threshold
     */
    @Override
    public void setThreshold(Serializable category, Double threshold) {
        thresholds.put(category, threshold);
    }

    /**
     * This method returns the minimum threshold considered for this category.
     *
     * @param category The category for which to set the minimum strength
     * @return the strength associated with the category
     */
    @Override
    public double getThreshold(Serializable category) {
        if (thresholds.containsKey(category)) {
            return thresholds.get(category);
        }
        return 1.0;
    }
}
