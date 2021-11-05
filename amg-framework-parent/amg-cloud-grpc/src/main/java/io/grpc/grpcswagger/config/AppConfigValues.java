package io.grpc.grpcswagger.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
//@PropertySource("classpath:config.properties")
public class AppConfigValues {
   
    @Value("${enable.list.service:true}")
    private boolean enableListService;
    
    @Value("#{${service.expired.seconds:0}}")
    private int serviceExpiredSeconds;
}
