package com.amg.framework.boot.redis.config;

import com.amg.framework.boot.redis.serializer.FastJson2JsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Autowired
    private RedisClusterConfig redisColonyConfig;

    @Bean
    public GenericObjectPoolConfig redisPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redisColonyConfig.getMaxIdle());
        config.setMaxIdle(redisColonyConfig.getMaxIdle());
        config.setMaxTotal(redisColonyConfig.getMaxActive());
        config.setMaxWaitMillis(redisColonyConfig.getMaxWait());
        return config;
    }

    @Bean
    public RedisConfiguration redisClusterConfiguration() {
        //RedisClusterConfiguration redisConfig = new RedisClusterConfiguration(redisColonyConfig.getNodes()); // 集群配置类
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(); // 单机配置
        redisConfiguration.setHostName(redisColonyConfig.getHost());
        redisConfiguration.setPassword(RedisPassword.of(redisColonyConfig.getPassword()));
        redisConfiguration.setPort(redisColonyConfig.getPort());
        return redisConfiguration;
    }

    @Bean
    //@Primary
    public LettuceConnectionFactory lettuceConnectionFactory(@Qualifier("redisPool") GenericObjectPoolConfig config,
                                            @Qualifier("redisClusterConfiguration") RedisConfiguration redisConfig) {
        return new LettuceConnectionFactory(redisConfig, LettucePoolingClientConfiguration.builder().poolConfig(config).build());
    }

    /**
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    //@Primary
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 开启事务
      //  redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        // 设置序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        FastJson2JsonRedisSerializer fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        fastJson2JsonRedisSerializer.setObjectMapper(objectMapper);

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

}
