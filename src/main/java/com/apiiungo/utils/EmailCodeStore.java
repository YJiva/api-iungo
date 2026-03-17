package com.apiiungo.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的内存验证码存储，用于本地开发环境，不依赖 Redis。
 */
@Component
public class EmailCodeStore {

    private static class Entry {
        final String code;
        final long expireAtMillis;

        Entry(String code, long expireAtMillis) {
            this.code = code;
            this.expireAtMillis = expireAtMillis;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public void setCode(String email, String code, long ttlMinutes) {
        long expireAt = Instant.now().toEpochMilli() + ttlMinutes * 60_000L;
        store.put(email, new Entry(code, expireAt));
    }

    public String getCode(String email) {
        Entry e = store.get(email);
        if (e == null) {
            return null;
        }
        if (Instant.now().toEpochMilli() > e.expireAtMillis) {
            store.remove(email);
            return null;
        }
        return e.code;
    }

    public void deleteCode(String email) {
        store.remove(email);
    }
}

