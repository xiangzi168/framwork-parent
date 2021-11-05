package com.amg.fulfillment.cloud.logistics.api.config.depository;

import cn.hutool.core.util.RandomUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-04-10-17:34
 */
@Component
@ConfigurationProperties(prefix = "depository.wanb")
@RefreshScope
@Data
public class WanBConfig {

    private String baseUrl;
    private String hc;
    private String accountNo;
    private String token;
    private String split=";";

    /**
     * Nounce为一个随机数，只能为字母、数字、短横线(-)或者下划线(_)等。该随机数不能为空。 在未来版本中，WanbExpress的服务器会限定同一个客户在一定的时间内 Nounce 值不能重复。
     * RandomUtil.randomString(5) 来实现 Nounce随机数
     */
    public String getAuthorization() {
        return hc+" "+accountNo+split+token+split+RandomUtil.randomString(5);
    }

//    public static void main(String[] args) {
//        // 生产
//        WanBConfig wanBConfig = new WanBConfig();
//        wanBConfig.setHc("Hc-OweDeveloper");
//        wanBConfig.setAccountNo("WNB02328");
//        wanBConfig.setToken("Y4QMpDeXM2bi5jd");
//        System.out.println(wanBConfig.getAuthorization());
//    }
}
