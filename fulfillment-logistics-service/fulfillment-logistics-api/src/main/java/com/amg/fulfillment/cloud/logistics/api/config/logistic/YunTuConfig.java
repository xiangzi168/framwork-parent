package com.amg.fulfillment.cloud.logistics.api.config.logistic;

import cn.hutool.core.codec.Base64Encoder;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-04-16-14:54
 */
@Component
@ConfigurationProperties(prefix = "logistic.yuntu")
@RefreshScope
@Data
public class YunTuConfig {

    private String userNo;
    private String apiSecret;
    private String baseUrl;
    private String authorization;


    public String getAuthorization() {
        if (StringUtils.isNotBlank(authorization)) {
            return "Basic "+authorization;
        }
        return "Basic "+ Base64Encoder.encode(userNo +"&"+apiSecret);
    }
}
