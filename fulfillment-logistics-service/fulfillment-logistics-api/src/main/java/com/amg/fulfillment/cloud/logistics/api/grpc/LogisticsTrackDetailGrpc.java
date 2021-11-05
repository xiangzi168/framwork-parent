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
    comments = "Source: logisticsTrackDetail.proto")
public final class LogisticsTrackDetailGrpc {

  private LogisticsTrackDetailGrpc() {}

  public static final String SERVICE_NAME = "logisticsTrack.LogisticsTrackDetail";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> getGetLogisticsTrackDetailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getLogisticsTrackDetail",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> getGetLogisticsTrackDetailMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> getGetLogisticsTrackDetailMethod;
    if ((getGetLogisticsTrackDetailMethod = LogisticsTrackDetailGrpc.getGetLogisticsTrackDetailMethod) == null) {
      synchronized (LogisticsTrackDetailGrpc.class) {
        if ((getGetLogisticsTrackDetailMethod = LogisticsTrackDetailGrpc.getGetLogisticsTrackDetailMethod) == null) {
          LogisticsTrackDetailGrpc.getGetLogisticsTrackDetailMethod = getGetLogisticsTrackDetailMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest, com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "logisticsTrack.LogisticsTrackDetail", "getLogisticsTrackDetail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply.getDefaultInstance()))
                  .setSchemaDescriptor(new LogisticsTrackDetailMethodDescriptorSupplier("getLogisticsTrackDetail"))
                  .build();
          }
        }
     }
     return getGetLogisticsTrackDetailMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LogisticsTrackDetailStub newStub(io.grpc.Channel channel) {
    return new LogisticsTrackDetailStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LogisticsTrackDetailBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new LogisticsTrackDetailBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LogisticsTrackDetailFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new LogisticsTrackDetailFutureStub(channel);
  }

  /**
   */
  public static abstract class LogisticsTrackDetailImplBase implements io.grpc.BindableService {

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponse getLogisticsTrackDetail(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetLogisticsTrackDetailMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply>(
                  this, METHODID_GET_LOGISTICS_TRACK_DETAIL)))
          .build();
    }
  }

  /**
   */
  public static final class LogisticsTrackDetailStub extends io.grpc.stub.AbstractStub<LogisticsTrackDetailStub> {
    private LogisticsTrackDetailStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsTrackDetailStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsTrackDetailStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsTrackDetailStub(channel, callOptions);
    }

    /**
     */
    public void getLogisticsTrackDetail(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLogisticsTrackDetailMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LogisticsTrackDetailBlockingStub extends io.grpc.stub.AbstractStub<LogisticsTrackDetailBlockingStub> {
    private LogisticsTrackDetailBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsTrackDetailBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsTrackDetailBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsTrackDetailBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply getLogisticsTrackDetail(com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLogisticsTrackDetailMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LogisticsTrackDetailFutureStub extends io.grpc.stub.AbstractStub<LogisticsTrackDetailFutureStub> {
    private LogisticsTrackDetailFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsTrackDetailFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsTrackDetailFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsTrackDetailFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply> getLogisticsTrackDetail(
        com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLogisticsTrackDetailMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_LOGISTICS_TRACK_DETAIL = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LogisticsTrackDetailImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LogisticsTrackDetailImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_LOGISTICS_TRACK_DETAIL:
          serviceImpl.getLogisticsTrackDetail((com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply>) responseObserver);
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

  private static abstract class LogisticsTrackDetailBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LogisticsTrackDetailBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LogisticsTrackDetail");
    }
  }

  private static final class LogisticsTrackDetailFileDescriptorSupplier
      extends LogisticsTrackDetailBaseDescriptorSupplier {
    LogisticsTrackDetailFileDescriptorSupplier() {}
  }

  private static final class LogisticsTrackDetailMethodDescriptorSupplier
      extends LogisticsTrackDetailBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LogisticsTrackDetailMethodDescriptorSupplier(String methodName) {
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
      synchronized (LogisticsTrackDetailGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LogisticsTrackDetailFileDescriptorSupplier())
              .addMethod(getGetLogisticsTrackDetailMethod())
              .build();
        }
      }
    }
    return result;
  }
}
