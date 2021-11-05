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
    comments = "Source: logisticsProvider.proto")
public final class LogisticsProviderGrpc {

  private LogisticsProviderGrpc() {}

  public static final String SERVICE_NAME = "LogisticsProvider";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getGetLogisticsProviderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getLogisticsProvider",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getGetLogisticsProviderMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getGetLogisticsProviderMethod;
    if ((getGetLogisticsProviderMethod = LogisticsProviderGrpc.getGetLogisticsProviderMethod) == null) {
      synchronized (LogisticsProviderGrpc.class) {
        if ((getGetLogisticsProviderMethod = LogisticsProviderGrpc.getGetLogisticsProviderMethod) == null) {
          LogisticsProviderGrpc.getGetLogisticsProviderMethod = getGetLogisticsProviderMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LogisticsProvider", "getLogisticsProvider"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new LogisticsProviderMethodDescriptorSupplier("getLogisticsProvider"))
                  .build();
          }
        }
     }
     return getGetLogisticsProviderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getGetLogisticsChannelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getLogisticsChannel",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getGetLogisticsChannelMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getGetLogisticsChannelMethod;
    if ((getGetLogisticsChannelMethod = LogisticsProviderGrpc.getGetLogisticsChannelMethod) == null) {
      synchronized (LogisticsProviderGrpc.class) {
        if ((getGetLogisticsChannelMethod = LogisticsProviderGrpc.getGetLogisticsChannelMethod) == null) {
          LogisticsProviderGrpc.getGetLogisticsChannelMethod = getGetLogisticsChannelMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LogisticsProvider", "getLogisticsChannel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new LogisticsProviderMethodDescriptorSupplier("getLogisticsChannel"))
                  .build();
          }
        }
     }
     return getGetLogisticsChannelMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getGetLogisticsRecommendProviderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getLogisticsRecommendProvider",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getGetLogisticsRecommendProviderMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getGetLogisticsRecommendProviderMethod;
    if ((getGetLogisticsRecommendProviderMethod = LogisticsProviderGrpc.getGetLogisticsRecommendProviderMethod) == null) {
      synchronized (LogisticsProviderGrpc.class) {
        if ((getGetLogisticsRecommendProviderMethod = LogisticsProviderGrpc.getGetLogisticsRecommendProviderMethod) == null) {
          LogisticsProviderGrpc.getGetLogisticsRecommendProviderMethod = getGetLogisticsRecommendProviderMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LogisticsProvider", "getLogisticsRecommendProvider"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new LogisticsProviderMethodDescriptorSupplier("getLogisticsRecommendProvider"))
                  .build();
          }
        }
     }
     return getGetLogisticsRecommendProviderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getGetLogisticsRecommendChannelMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getLogisticsRecommendChannel",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getGetLogisticsRecommendChannelMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getGetLogisticsRecommendChannelMethod;
    if ((getGetLogisticsRecommendChannelMethod = LogisticsProviderGrpc.getGetLogisticsRecommendChannelMethod) == null) {
      synchronized (LogisticsProviderGrpc.class) {
        if ((getGetLogisticsRecommendChannelMethod = LogisticsProviderGrpc.getGetLogisticsRecommendChannelMethod) == null) {
          LogisticsProviderGrpc.getGetLogisticsRecommendChannelMethod = getGetLogisticsRecommendChannelMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LogisticsProvider", "getLogisticsRecommendChannel"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new LogisticsProviderMethodDescriptorSupplier("getLogisticsRecommendChannel"))
                  .build();
          }
        }
     }
     return getGetLogisticsRecommendChannelMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LogisticsProviderStub newStub(io.grpc.Channel channel) {
    return new LogisticsProviderStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LogisticsProviderBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new LogisticsProviderBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LogisticsProviderFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new LogisticsProviderFutureStub(channel);
  }

  /**
   */
  public static abstract class LogisticsProviderImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponse> getLogisticsProvider(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsChannel(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponse> getLogisticsRecommendProvider(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponse> getLogisticsRecommendChannel(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetLogisticsProviderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult>(
                  this, METHODID_GET_LOGISTICS_PROVIDER)))
          .addMethod(
            getGetLogisticsChannelMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult>(
                  this, METHODID_GET_LOGISTICS_CHANNEL)))
          .addMethod(
            getGetLogisticsRecommendProviderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult>(
                  this, METHODID_GET_LOGISTICS_RECOMMEND_PROVIDER)))
          .addMethod(
            getGetLogisticsRecommendChannelMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult>(
                  this, METHODID_GET_LOGISTICS_RECOMMEND_CHANNEL)))
          .build();
    }
  }

  /**
   */
  public static final class LogisticsProviderStub extends io.grpc.stub.AbstractStub<LogisticsProviderStub> {
    private LogisticsProviderStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsProviderStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsProviderStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsProviderStub(channel, callOptions);
    }

    /**
     */
    public void getLogisticsProvider(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLogisticsProviderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getLogisticsChannel(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLogisticsChannelMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getLogisticsRecommendProvider(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLogisticsRecommendProviderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getLogisticsRecommendChannel(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLogisticsRecommendChannelMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LogisticsProviderBlockingStub extends io.grpc.stub.AbstractStub<LogisticsProviderBlockingStub> {
    private LogisticsProviderBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsProviderBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsProviderBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsProviderBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult getLogisticsProvider(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLogisticsProviderMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult getLogisticsChannel(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLogisticsChannelMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult getLogisticsRecommendProvider(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLogisticsRecommendProviderMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult getLogisticsRecommendChannel(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLogisticsRecommendChannelMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LogisticsProviderFutureStub extends io.grpc.stub.AbstractStub<LogisticsProviderFutureStub> {
    private LogisticsProviderFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsProviderFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsProviderFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsProviderFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getLogisticsProvider(
        com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLogisticsProviderMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getLogisticsChannel(
        com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLogisticsChannelMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult> getLogisticsRecommendProvider(
        com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLogisticsRecommendProviderMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult> getLogisticsRecommendChannel(
        com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLogisticsRecommendChannelMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_LOGISTICS_PROVIDER = 0;
  private static final int METHODID_GET_LOGISTICS_CHANNEL = 1;
  private static final int METHODID_GET_LOGISTICS_RECOMMEND_PROVIDER = 2;
  private static final int METHODID_GET_LOGISTICS_RECOMMEND_CHANNEL = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LogisticsProviderImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LogisticsProviderImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_LOGISTICS_PROVIDER:
          serviceImpl.getLogisticsProvider((com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.nullRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult>) responseObserver);
          break;
        case METHODID_GET_LOGISTICS_CHANNEL:
          serviceImpl.getLogisticsChannel((com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult>) responseObserver);
          break;
        case METHODID_GET_LOGISTICS_RECOMMEND_PROVIDER:
          serviceImpl.getLogisticsRecommendProvider((com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsProviderResponseResult>) responseObserver);
          break;
        case METHODID_GET_LOGISTICS_RECOMMEND_CHANNEL:
          serviceImpl.getLogisticsRecommendChannel((com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsRecommendRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.LogisticsChannelResponseResult>) responseObserver);
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

  private static abstract class LogisticsProviderBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LogisticsProviderBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.LogisticsProviderGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LogisticsProvider");
    }
  }

  private static final class LogisticsProviderFileDescriptorSupplier
      extends LogisticsProviderBaseDescriptorSupplier {
    LogisticsProviderFileDescriptorSupplier() {}
  }

  private static final class LogisticsProviderMethodDescriptorSupplier
      extends LogisticsProviderBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LogisticsProviderMethodDescriptorSupplier(String methodName) {
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
      synchronized (LogisticsProviderGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LogisticsProviderFileDescriptorSupplier())
              .addMethod(getGetLogisticsProviderMethod())
              .addMethod(getGetLogisticsChannelMethod())
              .addMethod(getGetLogisticsRecommendProviderMethod())
              .addMethod(getGetLogisticsRecommendChannelMethod())
              .build();
        }
      }
    }
    return result;
  }
}
