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

/**
 * This is a placeholder interface for the SimpleClassifier; it mostly exists
 * to provide a valid and cohesive hierarchy for the FisherClassifier.
 */
public interface SimpleClassifier extends Classifier {
    /**
     * TODO: Needs to be done
     *
     * @param category
     * @return
     */
    double getThreshold(Object category);

    /**
     * TODO: Needs to be done
     *
     * @param category
     * @param threshold
     */
    void setThreshold(Object category, Double threshold);
}
