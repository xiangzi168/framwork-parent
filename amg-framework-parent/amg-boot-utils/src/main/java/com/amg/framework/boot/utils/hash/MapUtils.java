package com.amg.framework.boot.utils.hash;

import java.util.*;
import java.util.stream.Collectors;


public class MapUtils {

    // 降序
    public static <K extends Comparable, V extends Object> Map<K, V> sortMapByKey(Map<K, V> map) {
        HashMap<K, V> finalOut = new LinkedHashMap<>();
        map.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getKey().compareTo(p1.getKey()))
                .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
        return finalOut;
    }

    // 降序
    public static <K extends Object, V extends Comparable> Map<K, V> sortMapByValue(Map<K, V> map) {
        HashMap<K, V> finalOut = new LinkedHashMap<>();
        map.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
        return finalOut;
    }

}
