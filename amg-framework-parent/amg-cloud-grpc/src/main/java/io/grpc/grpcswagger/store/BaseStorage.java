package io.grpc.grpcswagger.store;

import com.google.common.collect.ImmutableMap;

public interface BaseStorage<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    boolean exists(K key);
    ImmutableMap<K, V> getAll();
}
