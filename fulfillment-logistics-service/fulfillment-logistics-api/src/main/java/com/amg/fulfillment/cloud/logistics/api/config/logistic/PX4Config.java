package com.amg.fulfillment.cloud.logistics.api.config.logistic;

import cn.hutool.core.codec.Base64Encoder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-04-20-13:57
 */
@Component
@ConfigurationProperties(prefix = "logistic.px4")
@RefreshScope
@Data
public class PX4Config {

    private String baseUrl;
    private String appkey;
    private String appSecret;
    private String format;
    private String version_1_0;
    private String version_1_1;

}
