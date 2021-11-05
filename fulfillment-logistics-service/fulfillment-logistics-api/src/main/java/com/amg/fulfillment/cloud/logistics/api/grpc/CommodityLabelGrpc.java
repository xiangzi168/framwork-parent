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
    comments = "Source: CommodityLabel.proto")
public final class CommodityLabelGrpc {

  private CommodityLabelGrpc() {}

  public static final String SERVICE_NAME = "CommodityLabel";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> getGetCommodityLabelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getCommodityLabel",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> getGetCommodityLabelMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest, com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> getGetCommodityLabelMethod;
    if ((getGetCommodityLabelMethod = CommodityLabelGrpc.getGetCommodityLabelMethod) == null) {
      synchronized (CommodityLabelGrpc.class) {
        if ((getGetCommodityLabelMethod = CommodityLabelGrpc.getGetCommodityLabelMethod) == null) {
          CommodityLabelGrpc.getGetCommodityLabelMethod = getGetCommodityLabelMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest, com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "CommodityLabel", "getCommodityLabel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult.getDefaultInstance()))
                  .setSchemaDescriptor(new CommodityLabelMethodDescriptorSupplier("getCommodityLabel"))
                  .build();
          }
        }
     }
     return getGetCommodityLabelMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CommodityLabelStub newStub(io.grpc.Channel channel) {
    return new CommodityLabelStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CommodityLabelBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CommodityLabelBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CommodityLabelFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CommodityLabelFutureStub(channel);
  }

  /**
   */
  public static abstract class CommodityLabelImplBase implements io.grpc.BindableService {

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelInfoList getCommodityLabel(com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetCommodityLabelMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult>(
                  this, METHODID_GET_COMMODITY_LABEL)))
          .build();
    }
  }

  /**
   */
  public static final class CommodityLabelStub extends io.grpc.stub.AbstractStub<CommodityLabelStub> {
    private CommodityLabelStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CommodityLabelStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected CommodityLabelStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CommodityLabelStub(channel, callOptions);
    }

    /**
     */
    public void getCommodityLabel(com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetCommodityLabelMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CommodityLabelBlockingStub extends io.grpc.stub.AbstractStub<CommodityLabelBlockingStub> {
    private CommodityLabelBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CommodityLabelBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected CommodityLabelBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CommodityLabelBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult getCommodityLabel(com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetCommodityLabelMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CommodityLabelFutureStub extends io.grpc.stub.AbstractStub<CommodityLabelFutureStub> {
    private CommodityLabelFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CommodityLabelFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected CommodityLabelFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CommodityLabelFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult> getCommodityLabel(
        com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetCommodityLabelMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_COMMODITY_LABEL = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CommodityLabelImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CommodityLabelImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_COMMODITY_LABEL:
          serviceImpl.getCommodityLabel((com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.SkuListRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.CommodityLabelResult>) responseObserver);
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

  private static abstract class CommodityLabelBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CommodityLabelBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.CommodityLabelGto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CommodityLabel");
    }
  }

  private static final class CommodityLabelFileDescriptorSupplier
      extends CommodityLabelBaseDescriptorSupplier {
    CommodityLabelFileDescriptorSupplier() {}
  }

  private static final class CommodityLabelMethodDescriptorSupplier
      extends CommodityLabelBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CommodityLabelMethodDescriptorSupplier(String methodName) {
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
      synchronized (CommodityLabelGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CommodityLabelFileDescriptorSupplier())
              .addMethod(getGetCommodityLabelMethod())
              .build();
        }
      }
    }
    return result;
  }
}
