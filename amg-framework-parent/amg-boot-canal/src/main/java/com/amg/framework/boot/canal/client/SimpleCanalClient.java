package com.amg.framework.boot.canal.client;

import com.alibaba.otter.canal.client.CanalConnector;

import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.amg.framework.boot.canal.annotation.CanalEventPoint;
import com.amg.framework.boot.canal.annotation.CanalListener;
import com.amg.framework.boot.canal.config.CanalConfig;
import com.amg.framework.boot.canal.hander.CanalThreadUncaughtExceptionHandler;
import com.amg.framework.boot.canal.hander.ListenerContext;
import com.amg.framework.boot.canal.hander.ListenerPoint;
import com.amg.framework.boot.canal.hander.MessageHandler;
import com.amg.framework.boot.canal.util.ApplicationContextUtil;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author lyc
 * @date 2020/9/26 17:43
 * @describe
 */
public  class SimpleCanalClient implements CanalClient {
    private static Logger log = LoggerFactory.getLogger(SimpleCanalClient.class);

    private Thread.UncaughtExceptionHandler handler = new CanalThreadUncaughtExceptionHandler();

    private volatile boolean flag;

    private Thread workThread;

    private CanalConnector connector;
    /**
     * 默认 订阅所有 的库 下面所有的表
     */
    private String filter = ".*\\..*";
    //private String filter = "goods\\..*";

    private Integer batchSize = 4 * 1024;;

    private Long timeout = 1L;

    private TimeUnit unit = TimeUnit.SECONDS;

    private MessageHandler messageHandler;

    private String hostname;

    private Integer port;

    private String destination;

    private String username;

    private String password;

    private boolean clusterEnabled;


    private ListenerContext listenerContext;

    public SimpleCanalClient(MessageHandler messageHandler, String hostname, Integer port, String destination, String username, String password,String filter) {
        initListeners();
        this.messageHandler = messageHandler;
        this.hostname = hostname;
        this.port = port;
        this.destination = destination;
        this.username = username;
        this.password = password;
        this.clusterEnabled = false;
        this.filter = StringUtils.isBlank(filter)?this.filter:filter;
    }

    public SimpleCanalClient(MessageHandler messageHandler, CanalConfig canalConfig) {
        initListeners();
        this.messageHandler = messageHandler;
        this.hostname = canalConfig.getHost();
        this.port = canalConfig.getPort();
        this.destination = canalConfig.getDestination();
        this.username = canalConfig.getUserName();
        this.password = canalConfig.getPassword();
        Boolean clusterEnabled = canalConfig.getClusterEnabled();
        this.clusterEnabled = clusterEnabled==null?false:clusterEnabled;
        this.filter = StringUtils.isBlank(canalConfig.getFilter())?filter:canalConfig.getFilter();
    }

    private void initListeners() {
        log.info("{}: initializing the listeners....", Thread.currentThread().getName());
        Map<String, Object> listenerMap = ApplicationContextUtil.getBeansWithAnnotation(CanalListener.class);
        if (listenerMap != null) {
            for (Object target : listenerMap.values()) {
                Method[] methods = target.getClass().getDeclaredMethods();
                if (methods != null && methods.length > 0) {
                    for (Method method : methods) {
                        CanalEventPoint l = AnnotationUtils.findAnnotation(method, CanalEventPoint.class);
                        if (l != null) {
                            listenerContext.getAnnoListeners().add(new ListenerPoint(target, method, l));
                        }
                    }
                }
            }
        }

    }

    @Override
    public void start() {
        // 基于zookeeper动态获取canal server的地址，建立链接，其中一台server发生crash，可以支持failover
        if(this.clusterEnabled){
            this.connector  = CanalConnectors.newClusterConnector(hostname, destination, username, password);
        }else {
            this.connector = CanalConnectors.newSingleConnector(new InetSocketAddress(hostname, port), destination, username, password);
        }
        this.connector.connect();
        log.info("start canal client");
        this.connector.subscribe(filter);
        this.connector.rollback();
        flag = true;
        workThread = new Thread(new Runnable() {
            @Override
            public void run() {
                process();
            }
        });
        workThread.setName("canal-client-thread");
        workThread.setUncaughtExceptionHandler(handler);
        workThread.start();
    }

    @Override
    public void stop() {
        log.info("stop canal client");
        if (!flag) {
            return;
        }
        flag = false;
    }

    @Override
    public void process() {
        try {
            while (flag) {
                Message message = connector.getWithoutAck(batchSize,timeout,TimeUnit.SECONDS);
                long batchId = message.getId();
                if (message.getId() != -1 && message.getEntries().size() != 0) {
                    if(log.isDebugEnabled()){
                        log.debug("获取消息id {}", message.getId());
                    }
                    try {
                        messageHandler.handleMessage(message);
                    }catch (Exception e){
                        log.error("业务处理错误 {}",e);
                        connector.rollback(batchId);
                    }

                }else {
                    // 没有消息
                    Thread.sleep(100);
                }
                connector.ack(batchId);
            }
        } catch (Exception e) {
            log.error("canal client 异常", e);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e1) {
                // ignore
            }
        } finally {
            log.info("canal client close");
            stop();
            connector.disconnect();
        }
    }


    public void setConnector(CanalConnector connector) {
        this.connector = connector;
    }


    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }


    public CanalConnector getConnector() {
        return connector;
    }


    public MessageHandler getMessageHandler() {
        return messageHandler;
    }


    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        SimpleCanalClient.log = log;
    }

    public Thread.UncaughtExceptionHandler getHandler() {
        return handler;
    }

    public void setHandler(Thread.UncaughtExceptionHandler handler) {
        this.handler = handler;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Thread getWorkThread() {
        return workThread;
    }

    public void setWorkThread(Thread workThread) {
        this.workThread = workThread;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isClusterEnabled() {
        return clusterEnabled;
    }

    public void setClusterEnabled(boolean clusterEnabled) {
        this.clusterEnabled = clusterEnabled;
    }
}
