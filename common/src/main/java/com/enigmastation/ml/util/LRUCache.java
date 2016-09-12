package com.enigmastation.ml.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is an LRUCache that expands its max size by one for every ten cache overflows.
 *
 * @param <K> Key
 * @param <V> Value
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int maxSize;
    private int expansions = 0;

    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean returnVal = false;
        if (size() > maxSize) {
            expansions++;
            if (expansions < 10) {
                returnVal = true;
            } else {
                expansions = 0;
                maxSize++;
            }
        }
        return returnVal;
    }
}
