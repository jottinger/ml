package com.enigmastation.ml.bayes.impl;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorResult;
import java.util.*;

/**
 * User: jottinge
 * Date: 3/26/14
 * Time: 7:50 AM
 */

/**
 * User: jottinge
 * Date: 3/26/14
 * Time: 7:50 AM
 * TODO: Needs to work more on this.
 *
 * @param <K> is key
 * @param <V> is value
 */
public class CacheWrapper<K, V> implements Cache<K, V> {

    private final static String NOT_ALLOWED = "not allowed!";
    private final org.infinispan.Cache<K, V> internalCache;

    /**
     * Constructs cache
     *
     * @param cache
     */
    public CacheWrapper(org.infinispan.Cache<K, V> cache) {
        internalCache = cache;
    }

    @Override
    public V get(K k) {
        return internalCache.get(k);
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> ks) {
        Map<K, V> map = new HashMap<>();
        ks.stream().forEach(k -> map.put(k, internalCache.get(k)));
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean containsKey(K k) {
        return internalCache.containsKey(k);
    }

    @Override
    public void loadAll(Set<? extends K> ks, boolean b, CompletionListener completionListener) {
        throw new UnsupportedOperationException(NOT_ALLOWED);
    }

    @Override
    public void put(K k, V v) {
        internalCache.put(k, v);
    }

    @Override
    public V getAndPut(K k, V v) {
        return internalCache.put(k, v);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        internalCache.putAll(map);
    }

    @Override
    public boolean putIfAbsent(K k, V v) {
        return internalCache.putIfAbsent(k, v) == null;
    }

    @Override
    public boolean remove(K k) {
        return internalCache.remove(k) == null;
    }

    @Override
    public boolean remove(K k, V v) {
        return remove(k);
    }

    @Override
    public V getAndRemove(K k) {
        return internalCache.remove(k);
    }

    @Override
    public boolean replace(K k, V v, V v2) {
        return replace(k, v);
    }

    @Override
    public boolean replace(K k, V v) {
        return internalCache.put(k, v) == null;
    }

    @Override
    public V getAndReplace(K k, V v) {
        return internalCache.put(k, v);
    }

    @Override
    public void removeAll(Set<? extends K> ks) {
        ks.stream().forEach(internalCache::remove);
    }

    @Override
    public void removeAll() {
        internalCache.clear();
    }

    @Override
    public void clear() {
        internalCache.clear();
    }

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> cClass) {
        return null;
    }

    @Override
    public <T> T invoke(K k, EntryProcessor<K, V, T> kvtEntryProcessor, Object... objects) {
        return null;
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> ks, EntryProcessor<K, V, T> kvtEntryProcessor, Object... objects) {
        return null;
    }

    @Override
    public String getName() {
        return internalCache.getName();
    }

    @Override
    public CacheManager getCacheManager() {
        return null;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException(NOT_ALLOWED);
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> tClass) {
        return null;
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> kvCacheEntryListenerConfiguration) {
        throw new UnsupportedOperationException(NOT_ALLOWED);
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> kvCacheEntryListenerConfiguration) {
        throw new UnsupportedOperationException(NOT_ALLOWED);
    }

    @Override
    public Iterator<Cache.Entry<K, V>> iterator() {
        return null;
    }
}
