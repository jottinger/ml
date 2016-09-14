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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Needs to be done
 */
public class Feature implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    private static final String NOT_ALLOWED = "not allowed!";

    Object featureObject;
    Map<Object, Integer> categories = new HashMap<>();

    public Map<Object, Integer> getCategories() {
        return categories;
    }

    public void setCategories(Map<Object, Integer> categories) {
        this.categories = categories;
    }

    public Object getFeature() {
        return featureObject;
    }

    public void setFeature(Object feature) {
        this.featureObject = feature;
    }

    /**
     * TODO: Needs to be done
     *
     * @param category
     * @return
     */
    public Integer getCountForCategory(Object category) {
        Integer count = categories.get(category);
        if (count == null) {
            return 0;
        }
        return count;
    }

    /**
     * TODO: Needs to be done
     *
     * @param category
     */
    public void incrementCategoryCount(Object category) {
        Integer oldCount = categories.get(category);
        if (oldCount == null) {
            oldCount = 0;
        }
        categories.put(category, oldCount + 1);
    }

    /**
     * TODO: Needs to be done since a Serializable class should have all transient or serializable objects
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        throw new UnsupportedOperationException(NOT_ALLOWED);
    }

    /**
     * TODO: Needs to be done since a Serializable class should have all transient or serializable objects
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException(NOT_ALLOWED);
    }
}
