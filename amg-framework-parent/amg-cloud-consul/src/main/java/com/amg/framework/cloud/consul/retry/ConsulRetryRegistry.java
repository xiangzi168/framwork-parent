package com.amg.framework.cloud.consul.retry;

import cn.hutool.core.thread.ThreadUtil;
import com.amg.framework.cloud.consul.config.ConsulRetryProperties;
import com.ecwid.consul.v1.agent.model.NewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import java.util.List;


public class ConsulRetryRegistry implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConsulRetryRegistry.class);
    private ConsulAutoRegistration consulAutoRegistration;
    private ConsulServiceRegistry consulServiceRegistry;
    private DiscoveryClient discoveryClient;
    private ConsulRetryProperties properties;

    public ConsulRetryRegistry(
            ConsulAutoRegistration consulAutoRegistration,
            ConsulServiceRegistry consulServiceRegistry,
            DiscoveryClient discoveryClient,
            ConsulRetryProperties properties
    ) {
        this.consulAutoRegistration = consulAutoRegistration;
        this.consulServiceRegistry = consulServiceRegistry;
        this.discoveryClient = discoveryClient;
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        // 获取当前服务
        final NewService service = this.consulAutoRegistration.getService();
        // 启动一个线程进行服务监测
        ThreadUtil.newSingleExecutor().execute(
            ()->{
                log.info("consul服务监测已启动【{}】", service);
                while (true) {
                    try {
                        Thread.sleep(this.properties.getInitialInterval());
                    } catch (InterruptedException e) {
                        // 当前线程异常退出
                        log.error("consul服务已停止重新注册【{}】", service);
                        break;
                    }
                    // 健康检查
                    if (!this.checkStatus(service)) {
                        try {
                            // 重新注册
                            this.registry();
                            log.info("consul服务重新注册成功【{}】", service);
                        }catch (Exception e) {
                            log.warn("consul服务当前注册失败，准备下一次注册【{}】", service);
                        }
                    }
                }
            }
        );
    }

    /**
     * 服务注册
     */
    private void registry() {
        this.consulServiceRegistry.register(this.consulAutoRegistration);
    }

    /**
     * 检查服务状态
     * @param service 服务
     * @return 返回布尔值，正常true，异常false
     */
    private boolean checkStatus(NewService service) {
        // 检测标志
        boolean flag = false;
        try {
            // 获取所有服务实例
            List<ServiceInstance> instances = this.discoveryClient.getInstances(service.getName());
            // 遍历实例
            for (ServiceInstance instance : instances) {
                // 判断是否为当前服务
                if (instance.getInstanceId().equals(service.getId())) {
                    flag = true;
                    break;
                }
            }
        }catch (Exception e){}
        log.debug("consul服务心跳检测结束，检测结果为：{}", flag?"正常":"异常");
        return flag;
    }
}
