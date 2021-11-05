package io.grpc.grpcswagger.openapi.v2;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SwaggerV2DocumentView {

    private transient final SwaggerV2Documentation documentation;

    private transient final String serviceName;
    
    public SwaggerV2DocumentView(String serviceName, SwaggerV2Documentation documentation) {
        this.documentation = documentation;
        this.serviceName = serviceName;
    }
    
    public String getSwagger() {
        return documentation.getSwagger();
    }
    
    public InfoObject getInfo() {
        return documentation.getInfo();
    }
    
    public List<String> getProduces() {
        return documentation.getProduces();
    }
    
    public List<String> getConsumes() {
        return documentation.getConsumes();
    }
    
    public String getBasePath() {
        return documentation.getBasePath();
    }
    
    public String getHost() {
        return documentation.getHost();
    }
    
    public List<String> getSchemes() {
        return documentation.getSchemes();
    }
    
    public Map<String, DefinitionType> getDefinitions() {
        return documentation.getDefinitions();
    }

    public String getServiceName() {
        return serviceName;
    }

    public SwaggerV2Documentation getDocumentation() {
        return documentation;
    }
    
    public Map<String, PathItem> getPaths() {
        if (StringUtils.isBlank(serviceName)) {
            return documentation.getPaths();
        }
        return documentation.getPaths().entrySet().stream()
                .filter(e -> e.getKey().contains(serviceName + "."))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
