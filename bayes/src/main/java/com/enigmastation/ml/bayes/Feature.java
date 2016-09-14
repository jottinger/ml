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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: Needs to be done
 */
public class Feature implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private static final String NOT_ALLOWED = "not allowed!";

    private Serializable feature;

    private Map<Serializable, Integer> categories = new ConcurrentHashMap<>();

    public Map<Serializable, Integer> getCategories() {
        return categories;
    }

    public void setCategories(Map<Serializable, Integer> categories) {
        this.categories = categories;
    }

    public Object getFeature() {
        return feature;
    }

    public void setFeature(Serializable feature) {
        this.feature = feature;
    }

    public Integer getCountForCategory(Serializable category) {
        return categories.getOrDefault(category, 0);
    }

    public Feature(Serializable feature) {
        this.feature=feature;
    }

    /**
     * Increments the number of features for a given category
     *
     * @param category the category name
     */
    public void incrementCategoryCount(Serializable category) {
        categories.put(category,categories.getOrDefault(category,0)+1);
    }
}
