package io.grpc.grpcswagger.service;

import com.google.protobuf.DescriptorProtos;
import io.grpc.grpcswagger.manager.ServiceConfigManager;
import io.grpc.grpcswagger.model.RegisterParam;
import io.grpc.grpcswagger.model.ServiceConfig;
import io.grpc.grpcswagger.openapi.v2.*;
import io.swagger.models.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.grpc.grpcswagger.model.Result.error;
import static io.grpc.grpcswagger.utils.ServiceRegisterUtils.getServiceNames;
import static io.grpc.grpcswagger.utils.ServiceRegisterUtils.registerByIpAndPort;
import static java.util.stream.Collectors.toList;

@Service
public class DocumentService {

    private static List<SwaggerV2DocumentView> documentationCached = new ArrayList<>();

    private static List<String> serviceNames;

    public SwaggerV2Documentation getDocumentation(String service, String apiHost) {
        SwaggerV2Documentation swaggerV2Documentation = DocumentRegistry.getInstance().get(service);
        if (swaggerV2Documentation != null) {
            swaggerV2Documentation.setHost(apiHost);
        }
        return swaggerV2Documentation;
    }

    public void init() {
        RegisterParam param = new RegisterParam();
        param.setHost("127.0.0.1");
        param.setPort(9099);
        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets = registerByIpAndPort(param.getHost(), param.getPort());
//        if (CollectionUtils.isEmpty(fileDescriptorSets)) {
//            return error("no services find");
//        }
        serviceNames = getServiceNames(fileDescriptorSets);
        List<ServiceConfig> serviceConfigs = serviceNames.stream()
                .map(name -> new ServiceConfig(name, param.getHostAndPortText()))
                .peek(ServiceConfigManager::addServiceConfig)
                .collect(toList());

        for (String name : serviceNames) {
            SwaggerV2Documentation documentation = getDocumentation(name, param.getHost());
            SwaggerV2DocumentView swaggerV2DocumentView = new SwaggerV2DocumentView(name, documentation);
            buildDocumentation(swaggerV2DocumentView);
            setDocumentationCached(swaggerV2DocumentView);
        }
    }

    public List<SwaggerV2DocumentView> getDocumentationCached() {
        return documentationCached;
    }

    public void setDocumentationCached(SwaggerV2DocumentView swaggerV2Documentation) {
        documentationCached.add(swaggerV2Documentation);
    }

    public void buildDocumentation(SwaggerV2DocumentView swaggerV2DocumentView) {
        SwaggerV2Documentation swaggerV2Documentation = swaggerV2DocumentView.getDocumentation();
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("GRPC服务相关接口");
        tag.setDescription("GRPC SERVER API");
        tags.add(tag);
        swaggerV2Documentation.setTags(tags);
        Map<String, PathItem> paths = swaggerV2Documentation.getPaths();
        for (String key : paths.keySet()) {
            PathItem pathItem = paths.get(key);
            Operation operation = pathItem.getPost();
            operation.setTags(new ArrayList(){{add(tag.getName());}});
            operation.setOperationId((key + "POST").replaceAll("\\/", ""));
            operation.setSummary(key.replaceAll("\\/", ""));
            List<Parameter> parameters = pathItem.getPost().getParameters();
            for (Parameter p : parameters) {
                if (p instanceof QueryParameter
                        &&"headers".equals(p.getName())
                        && "query".equals(p.getIn())
                        && "Headers passed to gRPC server".equals(p.getDescription())
                        && "object".equals(((QueryParameter) p).getType())) {
                    ((QueryParameter) p).setType("string");
                    p.setDescription("gRPC server headers");
                }
            }
        }
    }

}
