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

package com.enigmastation.ml.bayes;

import java.util.Map;

/**
 * This is the base interface for the Bayesian classifiers.
 * Be aware that the API makes assumptions that tokenization is internal
 * to the classifier.
 */
public interface Classifier {
    /**
     * This returns the best-match classification from the bayesian engine.
     * The default classification is "none", which will be returned if
     * no other classification matches.
     *
     * @param source the source corpus for the classification operation
     * @return the best-match classification
     */
    Object classify(Object source);

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
     * @param source                the source corpus for the classification operation
     * @param defaultClassification the default classification
     * @return the best-match classification
     */
    Object classify(Object source, Object defaultClassification);

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
    Object classify(Object source, Object defaultClassification, double strength);

    /**
     * This method returns the raw classification data, as a map. The map contains
     * the classification as a key, which maps to the classification's strength.
     *
     * @param source the source corpus for the classification operation
     * @return A Map where the key is the classification category and the value
     * is the strength of that category
     */
    Map<Object, Double> getClassificationProbabilities(Object source);

    /**
     * This method trains the classifier.
     *
     * @param source         The source text for the training operation
     * @param classification The classification for which to train
     */
    void train(Object source, Object classification);
}
