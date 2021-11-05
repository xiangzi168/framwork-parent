package io.grpc.grpcswagger.controller;

import static io.grpc.CallOptions.DEFAULT;
import static io.grpc.grpcswagger.utils.GrpcReflectionUtils.parseToMethodDefinition;
import static java.util.Collections.singletonList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.amg.framework.boot.advice.annotation.IgnoreResponseAdvice;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import io.grpc.grpcswagger.utils.ChannelFactory;
import io.grpc.grpcswagger.utils.ServiceRegisterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HostAndPort;
import io.grpc.ManagedChannel;
import io.grpc.grpcswagger.manager.ServiceConfigManager;
import io.grpc.grpcswagger.model.CallResults;
import io.grpc.grpcswagger.model.GrpcMethodDefinition;
import io.grpc.grpcswagger.model.Result;
import io.grpc.grpcswagger.service.GrpcProxyService;
import lombok.SneakyThrows;


@RestController
@IgnoreResponseAdvice
public class GrpcController {

    private static final Logger logger = LoggerFactory.getLogger(GrpcController.class);

    private static final String ENDPOINT_PARAM = "endpoint";

//    @Autowired
//    private DocumentService documentService;
//
//    private static List<String> serviceNames;


   // @RequestMapping("/v2/api-docs")
   // @RequestMapping("/v2/swagger/api-docs")
   // @IgnoreResponseAdvice
//    public void groupResponse(HttpServletRequest httpServletRequest) {
//        String apiHost = httpServletRequest.getHeader("Host");
//        SwaggerV2Documentation documentation = documentService.getDocumentation("BasicCountryNameService", apiHost);
//        Utils.print(JSON.toJSONString(new SwaggerV2DocumentView("BasicCountryNameService", documentation)));
//       // return new SwaggerV2DocumentView("BasicCountryNameService", documentation);
//    }

    @SneakyThrows
    @ResponseBody
    public Object invokeMethod(HttpServletRequest request,
                                       @RequestBody String payload,
                                       @RequestParam(defaultValue = "{}") String headers) {
        GrpcMethodDefinition methodDefinition = parseToMethodDefinition(request.getRequestURI().replaceAll("\\/", ""));
        JSONObject jsonObject = JSON.parseObject(payload);
        HostAndPort endPoint;
        if (jsonObject.containsKey(ENDPOINT_PARAM)) {
            endPoint = HostAndPort.fromString(jsonObject.getString(ENDPOINT_PARAM));
            jsonObject.remove(ENDPOINT_PARAM);
            payload = JSON.toJSONString(jsonObject);
        } else {
            String fullServiceName = methodDefinition.getFullServiceName();
            endPoint = ServiceConfigManager.getEndPoint(fullServiceName);
        }
        if (endPoint == null) {
            return Result.success("can't find target endpoint");
        }
        Map<String, Object> metaHeaderMap = JSON.parseObject(headers);
        ManagedChannel channel = null;
        try {
            channel = ChannelFactory.create(endPoint, metaHeaderMap);
            CallResults results = SpringContextUtil.getBean(GrpcProxyService.class).invokeMethod(methodDefinition, ServiceRegisterUtils.channell, DEFAULT, singletonList(payload));
           // return Result.success(results.asJSON()).setEndpoint(endPoint.toString());
            return results.asList().get(0);
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }
    }


//    @RequestMapping("/listServices")
//    public Result<Object> listServices() {
//        if (!AppConfig.enableListService()) {
//            return Result.error("Not support this action.");
//        }
//       return Result.success(getServiceConfigs());
//    }

//    @RequestMapping("/register")
//    public Result<Object> registerServices(RegisterParam registerParam, HttpServletRequest httpServletRequest) {
//
//        List<FileDescriptorSet> fileDescriptorSets = registerByIpAndPort(registerParam.getHost(), registerParam.getPort());
//        if (CollectionUtils.isEmpty(fileDescriptorSets)) {
//            return error("no services find");
//        }
//        serviceNames = getServiceNames(fileDescriptorSets);
//        List<ServiceConfig> serviceConfigs = serviceNames.stream()
//                .map(name -> new ServiceConfig(name, registerParam.getHostAndPortText()))
//                .peek(ServiceConfigManager::addServiceConfig)
//                .collect(toList());
//
//
//        String apiHost = httpServletRequest.getHeader("Host");
//        SwaggerV2Documentation documentation = documentService.getDocumentation(serviceNames.get(0), apiHost);
//        SwaggerV2DocumentView swaggerV2DocumentView = new SwaggerV2DocumentView(serviceNames.get(0), documentation);
//        documentService.setDocumentationCached(swaggerV2DocumentView);
//
//        return Result.success(serviceConfigs);
//    }

}
