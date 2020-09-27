package com.supcon.mes.module_xj.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 作为临时存储的介质来存储bundle所不能存储的过大数据
 */
public class BundleSaveUtil {
    public static final BundleSaveUtil instance = new BundleSaveUtil();
    private Map<String, Object> bundleMap = new HashMap<>();

    public BundleSaveUtil put(String key, Object value) {
        bundleMap.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        T result = (T) bundleMap.get(key);
        bundleMap.remove(key);
        return result;
    }

    public boolean containsKey(String key) {
        return bundleMap.containsKey(key);
    }
}
