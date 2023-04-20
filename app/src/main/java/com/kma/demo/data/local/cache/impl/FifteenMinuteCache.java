package com.kma.demo.data.local.cache.impl;

import com.kma.demo.data.local.cache.Cache;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FifteenMinuteCache<K, V> implements Cache<K, V> {

    private final LinkedHashMap<K, V> cache = new LinkedHashMap<>(50, 0.75f, true);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void put(K key, V value) {
        if(value != null) {
            cache.put(key, value);
            scheduleExpiration(key);
        }
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private void scheduleExpiration(K key) {
        executorService.submit(() -> {
            try {
                Thread.sleep(60_000); // 15 minutes
                cache.remove(key);
            } catch (InterruptedException e) {
                // ignore
            }
        });
    }
}
