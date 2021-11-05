package com.amg.framework.boot.utils.id;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.amg.framework.boot.redis.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import static com.amg.framework.boot.utils.hash.HashUtils.getHashValue;


/**
 * 雪花算法生成分布式ID
 */
@Component
public class SnowflakeIdUtils {

    private static Logger logger = LoggerFactory.getLogger(SnowflakeIdUtils.class);

    @Autowired
    private RedisUtils redisUtils;

    @Value("${spring.application.name}")
    private String applicationName;

    private static Snowflake snowflake = null;

    @PostConstruct
    public void init() {
        long workerId = 0;
        long datacenterId = 0;
        try {
            workerId = redisUtils.incrt(applicationName);
            datacenterId = getHashValue(applicationName, 31);
            if (workerId > 31) {
                redisUtils.remove(applicationName);
                workerId = redisUtils.incrt(applicationName);
            }
        } catch (Exception e) {
            logger.error("【snowflakeIdUtils init error】", e);
        }
        snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    }


    /**
     * 获取分布式ID
     * @return
     */
    public static long getId() {
        return snowflake.nextId();
    }

}
