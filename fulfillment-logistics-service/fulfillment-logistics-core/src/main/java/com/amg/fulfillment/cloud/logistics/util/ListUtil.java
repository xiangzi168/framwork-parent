package com.amg.fulfillment.cloud.logistics.util;

import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName ArrayListUtil
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/13 10:35
 **/
public class ListUtil {
    public static List<List> segment(List list, Integer number) {
        if (number != null && number > 0 && list != null && list.size() > 0) {
            int limit = (list.size() + number - 1) / number;
            //方法一：使用流遍历操作
            List<List> mglist = new ArrayList<>();
            Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
                mglist.add((List) list.stream().skip(i * number).limit(number).collect(Collectors.toList()));
            });
            return mglist;
        }else{
            return null;
        }

    }
}
