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

public interface Classifier {
  Object classify(Object source);
  Object classify(Object source, Object defaultClassification);
  Object classify(Object source, Object defaultClassification, double strength);
  Map<Object, Double> getClassificationProbabilities(Object source);
  void train(Object source, Object classification);
}