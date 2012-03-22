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

import com.enigmastation.ml.tokenizer.Tokenizer;

import java.util.List;

public class ThreadLocalMemoizer {
    List<Object> features;
    Object corpus;

    public Object getCorpus() {
        return corpus;
    }

    public void setCorpus(Object corpus) {
        this.corpus = corpus;
    }

    public List<Object> getFeatures(Object corpus) {
        return features;
    }

    public void setFeatures(Object corpus, Tokenizer tokenizer) {
        if (!this.corpus.equals(corpus)) {
            this.corpus = corpus;
            this.features = tokenizer.tokenize(corpus);
        }
    }
}