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

import java.util.List;

public interface PerceptronRepository {
    double getStrength(int from, int to, Layer layer);

    void setStrength(int from, int to, Layer layer, double strength);

    List<Integer> getAllHiddenIds(List<Object> corpus, List<Object> targets);

    /*
    * This could use a cache *so* bad... assuming it's called a whole lot for the same terms.
    */
    int getNodeId(Object token, Layer layer, NodeCreation creation);

    void generateHiddenNodes(List<Object> corpus, List<Object> targets);

    /**
     * This is an enum that controls whether creating a node in the
     * repository is required or not.
     * <p/>
     * The cases in which it's not primarily focus around situations in which
     * the nodes have extra actions associated with creation.
     * <p/>
     * This is a PerceptronRepository-specific control.
     */
    enum NodeCreation {
        CREATE,
        NO_CREATE
    }
}
