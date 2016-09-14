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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleTokenizer implements Tokenizer {
    @Override
    public List<Serializable> tokenize(Serializable source) {
        String src = source.toString();
        List<Serializable> tokens = new ArrayList<>(src.length() / 6);
        Scanner scanner = new Scanner(src);
        while (scanner.hasNext("\\S*")) {
            tokens.add(scanner.next("\\S*"));
        }
        return tokens;
    }
}
