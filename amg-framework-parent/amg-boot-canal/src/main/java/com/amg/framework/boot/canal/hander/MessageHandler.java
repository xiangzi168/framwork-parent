package com.amg.framework.boot.canal.hander;


/**
 * @author lyc
 * @date 2020/9/26 17:43
 * @describe
 */
public interface MessageHandler<T> {

     void handleMessage(T t);

}
