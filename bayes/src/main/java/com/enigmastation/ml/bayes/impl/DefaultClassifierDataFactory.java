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

import com.enigmastation.ml.bayes.ClassifierDataFactory;
import com.enigmastation.ml.bayes.Feature;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.IOException;

/**
 * TODO: Needs to be done
 */
public class DefaultClassifierDataFactory implements ClassifierDataFactory {
    static EmbeddedCacheManager cacheManager;

    static {
        try {
            cacheManager = new DefaultCacheManager("bayes-cache.xml");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Cache<Object, Integer> buildCategories() {
        return getCache("categories");
    }

    private <K, V> Cache<K, V> getCache(String name) {
        return cacheManager.getCache(name, true);
    }

    @Override
    public Cache<Object, Feature> buildFeatures() {
        return getCache("features");
    }
}
