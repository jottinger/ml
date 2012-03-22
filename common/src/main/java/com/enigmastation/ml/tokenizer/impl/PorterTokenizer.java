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

package com.enigmastation.ml.tokenizer.impl;

import com.enigmastation.ml.tokenizer.Tokenizer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class PorterTokenizer implements Tokenizer {
    @Override
    public List<Object> tokenize(Object source) {
        List<Object> tokens = new ArrayList<>(source.toString().length() / 5);
        org.apache.lucene.analysis.Tokenizer tokenizer =
                new StandardTokenizer(Version.LUCENE_34,
                        new StringReader(source.toString()));
        CharTermAttribute charTermAttribute = tokenizer.getAttribute(CharTermAttribute.class);
        PorterStemFilter filter = new PorterStemFilter(tokenizer);
        try {
            while (filter.incrementToken()) {
                String term = charTermAttribute.toString().toLowerCase();
                if (term.length() > 2) {
                    tokens.add(term);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Should not happen: " + e.getMessage(), e);
        }
        return tokens;
    }
}
