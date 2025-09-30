package ru.otus.cachehw;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);

    private final WeakHashMap<K, V> cache = new WeakHashMap<>();
    private final List<WeakReference<HwListener<K, V>>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, "PUT");
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        if (value != null) {
            notifyListeners(key, value, "REMOVE");
        }
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            notifyListeners(key, value, "GET");
        }
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.removeIf(ref -> {
            HwListener<K, V> l = ref.get();
            return l == null || l == listener;
        });
    }

    private void notifyListeners(K key, V value, String action) {
        List<WeakReference<HwListener<K, V>>> toRemove = new ArrayList<>();

        for (WeakReference<HwListener<K, V>> ref : listeners) {
            HwListener<K, V> listener = ref.get();
            if (listener != null) {
                try {
                    listener.notify(key, value, action);
                } catch (Exception e) {
                    logger.error("Error notifying listener", e);
                }
            } else {
                toRemove.add(ref);
            }
        }

        listeners.removeAll(toRemove);
    }
}
