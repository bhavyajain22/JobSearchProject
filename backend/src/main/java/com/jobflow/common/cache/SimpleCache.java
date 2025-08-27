package com.jobflow.common.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SimpleCache<K,V> {

    private static class Entry<V> {
        final V value;
        final Instant expiresAt;
        Entry(V v, Instant ea) { this.value = v; this.expiresAt = ea; }
    }

    private final Map<K, Entry<V>> store = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final int maxEntries; // optional soft bound

    public SimpleCache(Duration ttl) { this(ttl, 1000); }
    public SimpleCache(Duration ttl, int maxEntries) {
        this.ttl = ttl;
        this.maxEntries = maxEntries;
    }

    public Optional<V> get(K key) {
        var e = store.get(key);
        if (e == null) return Optional.empty();
        if (e.expiresAt.isBefore(Instant.now())) {
            store.remove(key);
            return Optional.empty();
        }
        return Optional.ofNullable(e.value);
    }

    public V getOrCompute(K key, Supplier<V> supplier) {
        var hit = get(key);
        if (hit.isPresent()) return hit.get();
        // naive trim to avoid unbounded growth (good enough for MVP)
        if (store.size() > maxEntries) store.clear();
        V v = supplier.get();
        store.put(key, new Entry<>(v, Instant.now().plus(ttl)));
        return v;
    }

    public void put(K key, V value) {
        if (store.size() > maxEntries) store.clear();
        store.put(key, new Entry<>(value, Instant.now().plus(ttl)));
    }

    public void invalidate(K key) { store.remove(key); }
    public void clear() { store.clear(); }
}
