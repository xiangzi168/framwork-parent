package io.grpc.grpcswagger.grpc;

import com.amg.framework.boot.utils.spring.SpringContextUtil;
import io.grpc.grpcswagger.openapi.v2.SwaggerV2DocumentView;
import io.grpc.grpcswagger.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;


@Component
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private DocumentService documentService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if ("prod".equals(SpringContextUtil.getActiveProfile())) {
            documentService.init();
            buildRequestMapping(documentService.getDocumentationCached());
            File file = new File("/health");
            file.createNewFile();
        } else if ("test".equals(SpringContextUtil.getActiveProfile())) {
            documentService.init();
            buildRequestMapping(documentService.getDocumentationCached());
            File file = new File("/health");
            file.createNewFile();
        } else {
            documentService.init();
            buildRequestMapping(documentService.getDocumentationCached());
        }
    }


    private void buildRequestMapping(List<SwaggerV2DocumentView> swaggerV2DocumentViews) throws Exception {
        for (SwaggerV2DocumentView swaggerV2DocumentView : swaggerV2DocumentViews) {
            for (String key : swaggerV2DocumentView.getDocumentation().getPaths().keySet()) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = SpringContextUtil.getBean(RequestMappingHandlerMapping.class);
                Class entry = io.grpc.grpcswagger.controller.GrpcController.class;
                Method methodName = ReflectionUtils.findMethod(entry, "invokeMethod", HttpServletRequest.class, String.class, String.class);
                PatternsRequestCondition patterns = new PatternsRequestCondition(key);
                RequestMappingInfo mappingInfo = new RequestMappingInfo(patterns, null, null, null, null, null, null);
                requestMappingHandlerMapping.registerMapping(mappingInfo, entry.newInstance(), methodName);
            }
        }
    }

}
