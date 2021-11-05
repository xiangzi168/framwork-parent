package com.amg.framework.boot.redisson.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    @Configuration
    @ConditionalOnClass({Redisson.class})
    @ConditionalOnExpression("'${spring.redis.mode}'=='single' or '${spring.redis.mode}'=='cluster' or '${spring.redis.mode}'=='sentinel'")
    protected class RedissonSingleClientConfiguration {

        /**
         * 单机模式 redisson 客户端
         */

        @Bean
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "single")
        RedissonClient redissonSingle() {
            Config config = new Config();
            String node = redissonProperties.getHost() + ":" + redissonProperties.getPort();
            node = node.startsWith("redis://") ? node : "redis://" + node;
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress(node)
                    .setConnectTimeout(redissonProperties.getLettuce().getPool().getMaxWait())
                    .setTimeout(redissonProperties.getTimeout())
                    .setConnectionPoolSize(redissonProperties.getLettuce().getPool().getMaxActive())
                    .setConnectionMinimumIdleSize(redissonProperties.getLettuce().getPool().getMinIdle());
            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                serverConfig.setPassword(redissonProperties.getPassword());
            }
            return Redisson.create(config);
        }

        /**
         * 集群模式的 redisson 客户端
         *
         * @return
         */
        @Bean
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "cluster")
        RedissonClient redissonCluster() {
            Config config = new Config();
            List<String> nodes = redissonProperties.getCluster().getNodes();
            List<String> newNodes = new ArrayList<>();
            nodes.stream().forEach(e ->{
                newNodes.add(e.startsWith("redis://") ? e : "redis://" + e);
            });

            ClusterServersConfig serverConfig = config.useClusterServers()
                    .addNodeAddress(newNodes.toArray(new String[0]))
                    .setTimeout(redissonProperties.getTimeout());
            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                serverConfig.setPassword(redissonProperties.getPassword());
            }
            return Redisson.create(config);
        }

        /**
         * 哨兵模式 redisson 客户端
         * @return
         */

        @Bean
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "sentinel")
        RedissonClient redissonSentinel() {
            System.out.println("sentinel redisProperties:" + redissonProperties.getSentinel());
            Config config = new Config();
            List<String> nodes = redissonProperties.getSentinel().getNodes();
            List<String> newNodes = new ArrayList<>();
            nodes.stream().forEach(e ->{
                newNodes.add(e.startsWith("redis://") ? e : "redis://" + e);
            });

            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(newNodes.toArray(new String[0]))
                    .setMasterName(redissonProperties.getSentinel().getMaster())
                    .setReadMode(ReadMode.SLAVE)
                    .setFailedAttempts(redissonProperties.getSentinel().getFailMax())
                    .setTimeout(redissonProperties.getTimeout())
                    .setMasterConnectionPoolSize(redissonProperties.getLettuce().getPool().getMaxActive())
                    .setSlaveConnectionPoolSize(redissonProperties.getLettuce().getPool().getMaxActive());

            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                serverConfig.setPassword(redissonProperties.getPassword());
            }

            return Redisson.create(config);
        }
    }
}
