package ru.otus.cachehw;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MyCacheTest {

    @Test
    void testCacheOperations() {
        HwCache<String, String> cache = new MyCache<>();

        cache.put("key1", "value1");
        cache.put("key2", "value2");

        assertThat(cache.get("key1")).isEqualTo("value1");
        assertThat(cache.get("key2")).isEqualTo("value2");
        assertThat(cache.get("key3")).isNull();

        cache.remove("key1");
        assertThat(cache.get("key1")).isNull();
    }

    @Test
    void testCacheListeners() {
        HwCache<String, String> cache = new MyCache<>();

        StringBuilder events = new StringBuilder();
        HwListener<String, String> listener = (key, value, action) ->
                events.append(String.format("%s:%s", action, key)).append(";");

        cache.addListener(listener);

        cache.put("key1", "value1");
        cache.get("key1");
        cache.remove("key1");

        assertThat(events.toString()).contains("PUT:key1");
        assertThat(events.toString()).contains("GET:key1");
        assertThat(events.toString()).contains("REMOVE:key1");
    }
}
