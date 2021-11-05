package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.DeliveryPackageGTO;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.14.0)",
    comments = "Source: deliveryPackage.proto")
public final class DeliveryPackageGrpc {

  private DeliveryPackageGrpc() {}

  public static final String SERVICE_NAME = "DeliveryPackage";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<DeliveryPackageGTO.DeliveryPackageRequest,
      DeliveryPackageGTO.DeliveryPackageResponseResult> getPushAeLogisticsOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "pushAeLogisticsOrder",
      requestType = DeliveryPackageGTO.DeliveryPackageRequest.class,
      responseType = DeliveryPackageGTO.DeliveryPackageResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<DeliveryPackageGTO.DeliveryPackageRequest,
      DeliveryPackageGTO.DeliveryPackageResponseResult> getPushAeLogisticsOrderMethod() {
    io.grpc.MethodDescriptor<DeliveryPackageGTO.DeliveryPackageRequest, DeliveryPackageGTO.DeliveryPackageResponseResult> getPushAeLogisticsOrderMethod;
    if ((getPushAeLogisticsOrderMethod = DeliveryPackageGrpc.getPushAeLogisticsOrderMethod) == null) {
      synchronized (DeliveryPackageGrpc.class) {
        if ((getPushAeLogisticsOrderMethod = DeliveryPackageGrpc.getPushAeLogisticsOrderMethod) == null) {
          DeliveryPackageGrpc.getPushAeLogisticsOrderMethod = getPushAeLogisticsOrderMethod = 
              io.grpc.MethodDescriptor.<DeliveryPackageGTO.DeliveryPackageRequest, DeliveryPackageGTO.DeliveryPackageResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "DeliveryPackage", "pushAeLogisticsOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DeliveryPackageGTO.DeliveryPackageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DeliveryPackageGTO.DeliveryPackageResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new DeliveryPackageMethodDescriptorSupplier("pushAeLogisticsOrder"))
                  .build();
          }
        }
     }
     return getPushAeLogisticsOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DeliveryPackageStub newStub(io.grpc.Channel channel) {
    return new DeliveryPackageStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DeliveryPackageBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DeliveryPackageBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DeliveryPackageFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DeliveryPackageFutureStub(channel);
  }

  /**
   */
  public static abstract class DeliveryPackageImplBase implements io.grpc.BindableService {

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.DeliveryPackageGTO.DeliveryPackageDetail pushAeLogisticsOrder(DeliveryPackageGTO.DeliveryPackageRequest request,
                                     io.grpc.stub.StreamObserver<DeliveryPackageGTO.DeliveryPackageResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPushAeLogisticsOrderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                DeliveryPackageGTO.DeliveryPackageRequest,
                DeliveryPackageGTO.DeliveryPackageResponseResult>(
                  this, METHODID_PUSH_AE_LOGISTICS_ORDER)))
          .build();
    }
  }

  /**
   */
  public static final class DeliveryPackageStub extends io.grpc.stub.AbstractStub<DeliveryPackageStub> {
    private DeliveryPackageStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DeliveryPackageStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DeliveryPackageStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DeliveryPackageStub(channel, callOptions);
    }

    /**
     */
    public void pushAeLogisticsOrder(DeliveryPackageGTO.DeliveryPackageRequest request,
                                     io.grpc.stub.StreamObserver<DeliveryPackageGTO.DeliveryPackageResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPushAeLogisticsOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DeliveryPackageBlockingStub extends io.grpc.stub.AbstractStub<DeliveryPackageBlockingStub> {
    private DeliveryPackageBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DeliveryPackageBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DeliveryPackageBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DeliveryPackageBlockingStub(channel, callOptions);
    }

    /**
     */
    public DeliveryPackageGTO.DeliveryPackageResponseResult pushAeLogisticsOrder(DeliveryPackageGTO.DeliveryPackageRequest request) {
      return blockingUnaryCall(
          getChannel(), getPushAeLogisticsOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DeliveryPackageFutureStub extends io.grpc.stub.AbstractStub<DeliveryPackageFutureStub> {
    private DeliveryPackageFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DeliveryPackageFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DeliveryPackageFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DeliveryPackageFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DeliveryPackageGTO.DeliveryPackageResponseResult> pushAeLogisticsOrder(
        DeliveryPackageGTO.DeliveryPackageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPushAeLogisticsOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PUSH_AE_LOGISTICS_ORDER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DeliveryPackageImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DeliveryPackageImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PUSH_AE_LOGISTICS_ORDER:
          serviceImpl.pushAeLogisticsOrder((DeliveryPackageGTO.DeliveryPackageRequest) request,
              (io.grpc.stub.StreamObserver<DeliveryPackageGTO.DeliveryPackageResponseResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class DeliveryPackageBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DeliveryPackageBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return DeliveryPackageGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DeliveryPackage");
    }
  }

  private static final class DeliveryPackageFileDescriptorSupplier
      extends DeliveryPackageBaseDescriptorSupplier {
    DeliveryPackageFileDescriptorSupplier() {}
  }

  private static final class DeliveryPackageMethodDescriptorSupplier
      extends DeliveryPackageBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DeliveryPackageMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DeliveryPackageGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DeliveryPackageFileDescriptorSupplier())
              .addMethod(getPushAeLogisticsOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
