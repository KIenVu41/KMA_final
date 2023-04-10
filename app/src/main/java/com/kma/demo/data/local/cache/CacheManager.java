package com.kma.demo.data.local.cache;

import android.content.Context;
import android.os.Build;
import android.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class CacheManager {
    private LruCache<String, Object> cache;

    public CacheManager() {
        int cacheSize = 4 * 1024 * 1024; // 4MB
        cache = new LruCache<String, Object>(cacheSize) {
            @Override
            protected int sizeOf(String key, Object value) {
                return super.sizeOf(key, value);
            }
        };
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

}
