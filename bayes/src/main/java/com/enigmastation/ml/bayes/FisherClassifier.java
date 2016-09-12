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

public interface FisherClassifier extends SimpleClassifier {
    /**
     * This accesses the current minimum strength for the category. If the probability of a
     * classification operation is less than this strength for this category, the result is discarded.
     *
     * @param category The category for which to acquire the minimum strength
     * @return the strength
     */
    double getMinimum(Object category);

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
    void setMinimum(Object category, double strength);
}
