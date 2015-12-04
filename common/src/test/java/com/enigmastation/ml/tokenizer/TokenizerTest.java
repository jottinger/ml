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

package com.enigmastation.ml.tokenizer;

import com.enigmastation.ml.tokenizer.impl.PorterTokenizer;
import com.enigmastation.ml.tokenizer.impl.SimpleTokenizer;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TokenizerTest {
    @Test
    public void testSimpleTokenizer() {
        Tokenizer tokenizer = new SimpleTokenizer();
        Set<String> objects = tokenizer.tokenize("1 2 3 4 5 6 7");
        assertEquals(objects.size(), 7);
    }

    @DataProvider
    Object[][] stemmerTest() {
        return new Object[][]{
                {"Now is the time for all good men to come to the aid of their finalizing country.",
                        new String[]{"now", "time", "for", "all", "good", "men", "come", "aid", "their", "final", "countri"}},
                {"the quick brown fox jumps over the lazy dog's tail",
                        new String[]{"quick", "brown", "fox", "jump", "over", "lazi", "dog", "tail"}}
        };
    }

    @Test(dataProvider = "stemmerTest")
    public void testPorterTokenizer(String corpus, String[] tokens) {
        Tokenizer porterTokenizer = new PorterTokenizer();
        List<String> tokenList = Arrays.asList(tokens);
        Set<String> tokensFromStemmer = porterTokenizer.tokenize(corpus);
        assertEquals(tokensFromStemmer.stream().filter(tokenList::contains).count(), tokensFromStemmer.size());
        assertEquals(tokenList.stream().filter(tokensFromStemmer::contains).count(), tokenList.size());
    }
}
