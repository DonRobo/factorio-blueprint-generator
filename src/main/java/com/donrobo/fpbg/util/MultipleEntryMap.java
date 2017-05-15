package com.donrobo.fpbg.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultipleEntryMap<K, V> {

    private final HashMap<K, List<V>> map = new HashMap<>();

    public void put(K key, V value) {
        List<V> entryList = map.computeIfAbsent(key, k -> new ArrayList<>());

        entryList.add(value);
    }

    public List<V> get(K key) {
        map.computeIfAbsent(key, k -> new ArrayList<>());
        return map.get(key);
    }
}
