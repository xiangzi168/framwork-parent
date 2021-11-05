package io.grpc.grpcswagger.config;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AppConfig {
    
    private static AppConfigValues appConfigValues = new AppConfigValues();
   
    @Autowired
    public void setAppConfigValues(AppConfigValues values) {
        appConfigValues = values;
    }
    
    public static boolean enableListService() {
        
        return appConfigValues.isEnableListService();
    }
    
    public static int serviceExpiredSeconds() {
        return appConfigValues.getServiceExpiredSeconds();
    }

    @PostConstruct
    public void init() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
}
