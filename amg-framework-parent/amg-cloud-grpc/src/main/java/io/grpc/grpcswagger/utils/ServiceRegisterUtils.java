package io.grpc.grpcswagger.utils;

import static io.grpc.grpcswagger.openapi.v2.OpenApiParser.parseDefinition;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.net.HostAndPort;
import com.google.protobuf.DescriptorProtos;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.grpcswagger.grpc.ServiceResolver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceRegisterUtils {

    @GrpcClient(value = "${spring.application.name}")
    private Channel channel;

    public static Channel channell;

    private static final Set<String> blockServiceSet = new HashSet<>();

    static {
        blockServiceSet.add("grpc.health.v1.Health".toLowerCase());
        blockServiceSet.add("grpc.reflection.v1alpha.ServerReflection".toLowerCase());
    }

    @PostConstruct
    public void init() {
        channell = channel;
    }

    public static List<DescriptorProtos.FileDescriptorSet> registerByIpAndPort(String hostAndPort) {
        String[] strings = hostAndPort.split(":");
        if (strings.length != 2) {
            return emptyList();
        }
        return registerByIpAndPort(strings[0], Integer.parseInt(strings[1]));
    }

    public static List<DescriptorProtos.FileDescriptorSet> registerByIpAndPort(String host, int port) {
        HostAndPort hostAndPort = HostAndPort.fromParts(host, port);
//        Channel channel = ManagedChannelBuilder.forAddress(hostAndPort.getHost(), hostAndPort.getPort())
//                .usePlaintext()
//                .build();

        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets = GrpcReflectionUtils.resolveServices(channell);
        fileDescriptorSets.forEach(fileDescriptorSet -> {
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            parseDefinition(serviceResolver);
        });
        return fileDescriptorSets;
    }

    public static List<String> getServiceNames(List<DescriptorProtos.FileDescriptorSet> fileDescriptorSets) {
        List<String> serviceNames = new ArrayList<>();
        fileDescriptorSets.forEach(fileDescriptorSet -> {
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
            serviceResolver.listServices().forEach(serviceDescriptor -> {
                String serviceName = serviceDescriptor.getFullName();
                if (blockServiceSet.contains(serviceName.toLowerCase())) {
                    return;
                }
                serviceNames.add(serviceName);
            });
        });
        return serviceNames.stream().distinct().sorted().collect(Collectors.toList());
    }
}
