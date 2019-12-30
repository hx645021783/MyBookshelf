//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.jaredhuang.basemvplib;

import androidx.collection.ArrayMap;

public class BitIntentDataManager {
    private static ArrayMap<String, Object> bigData;

    private static BitIntentDataManager instance = null;

    private BitIntentDataManager() {
        bigData = new ArrayMap<>();
    }

    public static BitIntentDataManager getInstance() {
        if (instance == null) {
            synchronized (BitIntentDataManager.class) {
                if (instance == null) {
                    instance = new BitIntentDataManager();
                }
            }
        }
        return instance;
    }

    public Object getData(String key) {
        Object object = bigData.get(key);
        bigData.remove(key);
        return object;
    }

    public void putData(String key, Object data) {
        bigData.put(key, data);
    }

}
