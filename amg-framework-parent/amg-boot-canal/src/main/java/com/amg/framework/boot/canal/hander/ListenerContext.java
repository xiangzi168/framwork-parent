package com.amg.framework.boot.canal.hander;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lyc
 * @date 2020/10/10 9:55
 * @describe
 */
public class ListenerContext {

    private static List<ListenerPoint> annoListeners = new ArrayList<>();

    public static List<ListenerPoint> getAnnoListeners() {
        return annoListeners;
    }
}
