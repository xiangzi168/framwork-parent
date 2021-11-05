package com.amg.fulfillment.cloud.logistics.api.config.logistic;

import cn.hutool.core.codec.Base64Encoder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-04-16-14:54
 */
@Component
@ConfigurationProperties(prefix = "logistic.yanwen")
@RefreshScope
@Data
public class YanWenConfig {

    private String userId;
    private String authorization;
    private String baseUrl;
    private String trackUrl;


    public String getAuthorization() {
        return "Basic " + authorization;
    }
}
