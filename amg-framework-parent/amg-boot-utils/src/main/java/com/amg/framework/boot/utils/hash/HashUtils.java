package com.amg.framework.boot.utils.hash;

public class HashUtils {

    public static int getHashValue(Object obj, int length) {
        return getIndex(hash(obj), length);
    }

    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private static int getIndex(int h, int length) {
        return h & (length - 1);
    }
}
