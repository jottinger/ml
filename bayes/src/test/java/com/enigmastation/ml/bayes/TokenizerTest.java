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

import com.enigmastation.ml.bayes.impl.PorterTokenizer;
import com.enigmastation.ml.bayes.impl.SimpleTokenizer;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class TokenizerTest {
  @Test
  public void testSimpleTokenizer() {
    Tokenizer tokenizer = new SimpleTokenizer();
    List<Object> objects = tokenizer.tokenize("1 2 3 4 5 6 7");
    assertEquals(objects.size(), 7);
  }

  @Test
  public void testPorterTokenizer() {
    Tokenizer porterTokenizer = new PorterTokenizer();
    porterTokenizer.tokenize("Now is the time for all good men to come to the aid of their finalizing country.");
    assertEquals(porterTokenizer.tokenize("the quick brown fox jumps over the lazy dog's tail").size(), 10);
  }
}
