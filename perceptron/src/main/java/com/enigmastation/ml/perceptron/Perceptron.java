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

import com.enigmastation.ml.tokenizer.Tokenizer;

import java.util.List;
import java.util.Queue;

public interface Perceptron {
    PerceptronState buildPerceptron(List<Object> corpus, List<Object> targets);

    void train(List<Object> corpus, List<Object> targets, Object selected);

    Queue<PerceptronResult> getResults(List<Object> corpus, List<Object> targets);

    void train(Object corpus, List<Object> targets, Object selected);

    /**
     * Trains the perceptron for all targets given the corpus and the preferred target.
     * Note that this method is far, far slower than the version in which targets are
     * supplied!
     *
     * @param corpus   the body of tokenized text for which to train
     * @param selected the preferred target
     */
    void train(Object corpus, Object selected);

    void createTarget(Object target);

    void train(Object corpus, Object[] targets, Object selected);

    Tokenizer getTokenizer();

    void setTokenizer(Tokenizer tokenizer);

    Queue<PerceptronResult> getResults(Object corpus, List<Object> targets);

    Queue<PerceptronResult> getResults(Object corpus, Object[] targets);

    Object getFirstResult(List<Object> corpus, List<Object> targets);

    Object getFirstResult(Object corpus, List<Object> targets);

    Object getFirstResult(Object corpus, Object[] targets);

    Object getFirstResult(Object corpus);

    Queue<PerceptronResult> getResults(Object corpus);

    List<Object> getAllTargets();
}
