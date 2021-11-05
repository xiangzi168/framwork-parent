package com.amg.fulfillment.cloud.logistics.api.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.14.0)",
    comments = "Source: purchasePackageLogisticsInfo.proto")
public final class PurchasePackageLogisticsInfoGrpc {

  private PurchasePackageLogisticsInfoGrpc() {}

  public static final String SERVICE_NAME = "PurchasePackageLogisticsInfo";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> getGetPurchasePackageLogisticsInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getPurchasePackageLogisticsInfo",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> getGetPurchasePackageLogisticsInfoMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest, com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> getGetPurchasePackageLogisticsInfoMethod;
    if ((getGetPurchasePackageLogisticsInfoMethod = PurchasePackageLogisticsInfoGrpc.getGetPurchasePackageLogisticsInfoMethod) == null) {
      synchronized (PurchasePackageLogisticsInfoGrpc.class) {
        if ((getGetPurchasePackageLogisticsInfoMethod = PurchasePackageLogisticsInfoGrpc.getGetPurchasePackageLogisticsInfoMethod) == null) {
          PurchasePackageLogisticsInfoGrpc.getGetPurchasePackageLogisticsInfoMethod = getGetPurchasePackageLogisticsInfoMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest, com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "PurchasePackageLogisticsInfo", "getPurchasePackageLogisticsInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult.getDefaultInstance()))
                  .setSchemaDescriptor(new PurchasePackageLogisticsInfoMethodDescriptorSupplier("getPurchasePackageLogisticsInfo"))
                  .build();
          }
        }
     }
     return getGetPurchasePackageLogisticsInfoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PurchasePackageLogisticsInfoStub newStub(io.grpc.Channel channel) {
    return new PurchasePackageLogisticsInfoStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PurchasePackageLogisticsInfoBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PurchasePackageLogisticsInfoBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PurchasePackageLogisticsInfoFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PurchasePackageLogisticsInfoFutureStub(channel);
  }

  /**
   */
  public static abstract class PurchasePackageLogisticsInfoImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.logisticsInfo> getPurchasePackageLogisticsInfo(com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> responseObserver) {
			return null;
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetPurchasePackageLogisticsInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult>(
                  this, METHODID_GET_PURCHASE_PACKAGE_LOGISTICS_INFO)))
          .build();
    }
  }

  /**
   */
  public static final class PurchasePackageLogisticsInfoStub extends io.grpc.stub.AbstractStub<PurchasePackageLogisticsInfoStub> {
    private PurchasePackageLogisticsInfoStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PurchasePackageLogisticsInfoStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PurchasePackageLogisticsInfoStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PurchasePackageLogisticsInfoStub(channel, callOptions);
    }

    /**
     */
    public void getPurchasePackageLogisticsInfo(com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetPurchasePackageLogisticsInfoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PurchasePackageLogisticsInfoBlockingStub extends io.grpc.stub.AbstractStub<PurchasePackageLogisticsInfoBlockingStub> {
    private PurchasePackageLogisticsInfoBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PurchasePackageLogisticsInfoBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PurchasePackageLogisticsInfoBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PurchasePackageLogisticsInfoBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult getPurchasePackageLogisticsInfo(com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetPurchasePackageLogisticsInfoMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PurchasePackageLogisticsInfoFutureStub extends io.grpc.stub.AbstractStub<PurchasePackageLogisticsInfoFutureStub> {
    private PurchasePackageLogisticsInfoFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PurchasePackageLogisticsInfoFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PurchasePackageLogisticsInfoFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PurchasePackageLogisticsInfoFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult> getPurchasePackageLogisticsInfo(
        com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetPurchasePackageLogisticsInfoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PURCHASE_PACKAGE_LOGISTICS_INFO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PurchasePackageLogisticsInfoImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PurchasePackageLogisticsInfoImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PURCHASE_PACKAGE_LOGISTICS_INFO:
          serviceImpl.getPurchasePackageLogisticsInfo((com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PurchasePackageLogisticsInfoBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PurchasePackageLogisticsInfoBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.PurchasePackageLogisticsInfoGTO.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PurchasePackageLogisticsInfo");
    }
  }

  private static final class PurchasePackageLogisticsInfoFileDescriptorSupplier
      extends PurchasePackageLogisticsInfoBaseDescriptorSupplier {
    PurchasePackageLogisticsInfoFileDescriptorSupplier() {}
  }

  private static final class PurchasePackageLogisticsInfoMethodDescriptorSupplier
      extends PurchasePackageLogisticsInfoBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PurchasePackageLogisticsInfoMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PurchasePackageLogisticsInfoGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PurchasePackageLogisticsInfoFileDescriptorSupplier())
              .addMethod(getGetPurchasePackageLogisticsInfoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
