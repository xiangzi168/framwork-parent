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
    comments = "Source: channelOrderForDirectSend.proto")
public final class ChannelOrderForDirectSendGrpc {

  private ChannelOrderForDirectSendGrpc() {}

  public static final String SERVICE_NAME = "ChannelOrderForDirectSend";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> getPushDeliveryPackMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "pushDeliveryPack",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> getPushDeliveryPackMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest, com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> getPushDeliveryPackMethod;
    if ((getPushDeliveryPackMethod = ChannelOrderForDirectSendGrpc.getPushDeliveryPackMethod) == null) {
      synchronized (ChannelOrderForDirectSendGrpc.class) {
        if ((getPushDeliveryPackMethod = ChannelOrderForDirectSendGrpc.getPushDeliveryPackMethod) == null) {
          ChannelOrderForDirectSendGrpc.getPushDeliveryPackMethod = getPushDeliveryPackMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest, com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderForDirectSend", "pushDeliveryPack"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderForDirectSendMethodDescriptorSupplier("pushDeliveryPack"))
                  .build();
          }
        }
     }
     return getPushDeliveryPackMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> getQueryChannelOrderForDirectSendMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "queryChannelOrderForDirectSend",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> getQueryChannelOrderForDirectSendMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest, com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> getQueryChannelOrderForDirectSendMethod;
    if ((getQueryChannelOrderForDirectSendMethod = ChannelOrderForDirectSendGrpc.getQueryChannelOrderForDirectSendMethod) == null) {
      synchronized (ChannelOrderForDirectSendGrpc.class) {
        if ((getQueryChannelOrderForDirectSendMethod = ChannelOrderForDirectSendGrpc.getQueryChannelOrderForDirectSendMethod) == null) {
          ChannelOrderForDirectSendGrpc.getQueryChannelOrderForDirectSendMethod = getQueryChannelOrderForDirectSendMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest, com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderForDirectSend", "queryChannelOrderForDirectSend"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderForDirectSendMethodDescriptorSupplier("queryChannelOrderForDirectSend"))
                  .build();
          }
        }
     }
     return getQueryChannelOrderForDirectSendMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ChannelOrderForDirectSendStub newStub(io.grpc.Channel channel) {
    return new ChannelOrderForDirectSendStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ChannelOrderForDirectSendBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ChannelOrderForDirectSendBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ChannelOrderForDirectSendFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ChannelOrderForDirectSendFutureStub(channel);
  }

  /**
   */
  public static abstract class ChannelOrderForDirectSendImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendDetail> pushDeliveryPack(com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendDetail> queryChannelOrderForDirectSend(com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPushDeliveryPackMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult>(
                  this, METHODID_PUSH_DELIVERY_PACK)))
          .addMethod(
            getQueryChannelOrderForDirectSendMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult>(
                  this, METHODID_QUERY_CHANNEL_ORDER_FOR_DIRECT_SEND)))
          .build();
    }
  }

  /**
   */
  public static final class ChannelOrderForDirectSendStub extends io.grpc.stub.AbstractStub<ChannelOrderForDirectSendStub> {
    private ChannelOrderForDirectSendStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChannelOrderForDirectSendStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ChannelOrderForDirectSendStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChannelOrderForDirectSendStub(channel, callOptions);
    }

    /**
     */
    public void pushDeliveryPack(com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPushDeliveryPackMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void queryChannelOrderForDirectSend(com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getQueryChannelOrderForDirectSendMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ChannelOrderForDirectSendBlockingStub extends io.grpc.stub.AbstractStub<ChannelOrderForDirectSendBlockingStub> {
    private ChannelOrderForDirectSendBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChannelOrderForDirectSendBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ChannelOrderForDirectSendBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChannelOrderForDirectSendBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult pushDeliveryPack(com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request) {
      return blockingUnaryCall(
          getChannel(), getPushDeliveryPackMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult queryChannelOrderForDirectSend(com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request) {
      return blockingUnaryCall(
          getChannel(), getQueryChannelOrderForDirectSendMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ChannelOrderForDirectSendFutureStub extends io.grpc.stub.AbstractStub<ChannelOrderForDirectSendFutureStub> {
    private ChannelOrderForDirectSendFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChannelOrderForDirectSendFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ChannelOrderForDirectSendFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChannelOrderForDirectSendFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult> pushDeliveryPack(
        com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPushDeliveryPackMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult> queryChannelOrderForDirectSend(
        com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getQueryChannelOrderForDirectSendMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PUSH_DELIVERY_PACK = 0;
  private static final int METHODID_QUERY_CHANNEL_ORDER_FOR_DIRECT_SEND = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ChannelOrderForDirectSendImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ChannelOrderForDirectSendImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PUSH_DELIVERY_PACK:
          serviceImpl.pushDeliveryPack((com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendResult>) responseObserver);
          break;
        case METHODID_QUERY_CHANNEL_ORDER_FOR_DIRECT_SEND:
          serviceImpl.queryChannelOrderForDirectSend((com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendResult>) responseObserver);
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

  private static abstract class ChannelOrderForDirectSendBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ChannelOrderForDirectSendBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderForDirectSendGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ChannelOrderForDirectSend");
    }
  }

  private static final class ChannelOrderForDirectSendFileDescriptorSupplier
      extends ChannelOrderForDirectSendBaseDescriptorSupplier {
    ChannelOrderForDirectSendFileDescriptorSupplier() {}
  }

  private static final class ChannelOrderForDirectSendMethodDescriptorSupplier
      extends ChannelOrderForDirectSendBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ChannelOrderForDirectSendMethodDescriptorSupplier(String methodName) {
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
      synchronized (ChannelOrderForDirectSendGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ChannelOrderForDirectSendFileDescriptorSupplier())
              .addMethod(getPushDeliveryPackMethod())
              .addMethod(getQueryChannelOrderForDirectSendMethod())
              .build();
        }
      }
    }
    return result;
  }
}
