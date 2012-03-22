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

public enum Layer {
    FROM,
    HIDDEN("wordhidden", -0.2),
    TO("hiddenword", 0.0);
    String tableName;
    double strength = 0.0;

    Layer() {
    }

    Layer(String tableName, double strength) {
        this.tableName = tableName;
        this.strength = strength;
    }

    public double getStrength() {
        return strength;
    }

    public String getTableName() {
        if (tableName == null) {
            throw new IllegalArgumentException("tableName not implemented for " + this);
        }
        return tableName;
    }
}
