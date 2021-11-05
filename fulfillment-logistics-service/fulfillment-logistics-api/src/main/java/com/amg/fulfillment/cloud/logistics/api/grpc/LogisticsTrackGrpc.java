package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackGTO;

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
    comments = "Source: logisticsTrack.proto")
public final class LogisticsTrackGrpc {

  private LogisticsTrackGrpc() {}

  public static final String SERVICE_NAME = "LogisticsTrack";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<LogisticsTrackGTO.LogisticsTrackRequest,
      LogisticsTrackGTO.LogisticsTrackResponseResult> getGetLogisticsTrackMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getLogisticsTrack",
      requestType = LogisticsTrackGTO.LogisticsTrackRequest.class,
      responseType = LogisticsTrackGTO.LogisticsTrackResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<LogisticsTrackGTO.LogisticsTrackRequest,
      LogisticsTrackGTO.LogisticsTrackResponseResult> getGetLogisticsTrackMethod() {
    io.grpc.MethodDescriptor<LogisticsTrackGTO.LogisticsTrackRequest, LogisticsTrackGTO.LogisticsTrackResponseResult> getGetLogisticsTrackMethod;
    if ((getGetLogisticsTrackMethod = LogisticsTrackGrpc.getGetLogisticsTrackMethod) == null) {
      synchronized (LogisticsTrackGrpc.class) {
        if ((getGetLogisticsTrackMethod = LogisticsTrackGrpc.getGetLogisticsTrackMethod) == null) {
          LogisticsTrackGrpc.getGetLogisticsTrackMethod = getGetLogisticsTrackMethod = 
              io.grpc.MethodDescriptor.<LogisticsTrackGTO.LogisticsTrackRequest, LogisticsTrackGTO.LogisticsTrackResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "LogisticsTrack", "getLogisticsTrack"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  LogisticsTrackGTO.LogisticsTrackRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  LogisticsTrackGTO.LogisticsTrackResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new LogisticsTrackMethodDescriptorSupplier("getLogisticsTrack"))
                  .build();
          }
        }
     }
     return getGetLogisticsTrackMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static LogisticsTrackStub newStub(io.grpc.Channel channel) {
    return new LogisticsTrackStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static LogisticsTrackBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new LogisticsTrackBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static LogisticsTrackFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new LogisticsTrackFutureStub(channel);
  }

  /**
   */
  public static abstract class LogisticsTrackImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackGTO.LogisticsTrackResponse> getLogisticsTrack(LogisticsTrackGTO.LogisticsTrackRequest request,
                                  io.grpc.stub.StreamObserver<LogisticsTrackGTO.LogisticsTrackResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetLogisticsTrackMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                LogisticsTrackGTO.LogisticsTrackRequest,
                LogisticsTrackGTO.LogisticsTrackResponseResult>(
                  this, METHODID_GET_LOGISTICS_TRACK)))
          .build();
    }
  }

  /**
   */
  public static final class LogisticsTrackStub extends io.grpc.stub.AbstractStub<LogisticsTrackStub> {
    private LogisticsTrackStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsTrackStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsTrackStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsTrackStub(channel, callOptions);
    }

    /**
     */
    public void getLogisticsTrack(LogisticsTrackGTO.LogisticsTrackRequest request,
                                  io.grpc.stub.StreamObserver<LogisticsTrackGTO.LogisticsTrackResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetLogisticsTrackMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class LogisticsTrackBlockingStub extends io.grpc.stub.AbstractStub<LogisticsTrackBlockingStub> {
    private LogisticsTrackBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsTrackBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsTrackBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsTrackBlockingStub(channel, callOptions);
    }

    /**
     */
    public LogisticsTrackGTO.LogisticsTrackResponseResult getLogisticsTrack(LogisticsTrackGTO.LogisticsTrackRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetLogisticsTrackMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class LogisticsTrackFutureStub extends io.grpc.stub.AbstractStub<LogisticsTrackFutureStub> {
    private LogisticsTrackFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private LogisticsTrackFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected LogisticsTrackFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new LogisticsTrackFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<LogisticsTrackGTO.LogisticsTrackResponseResult> getLogisticsTrack(
        LogisticsTrackGTO.LogisticsTrackRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetLogisticsTrackMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_LOGISTICS_TRACK = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final LogisticsTrackImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(LogisticsTrackImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_LOGISTICS_TRACK:
          serviceImpl.getLogisticsTrack((LogisticsTrackGTO.LogisticsTrackRequest) request,
              (io.grpc.stub.StreamObserver<LogisticsTrackGTO.LogisticsTrackResponseResult>) responseObserver);
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

  private static abstract class LogisticsTrackBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    LogisticsTrackBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return LogisticsTrackGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("LogisticsTrack");
    }
  }

  private static final class LogisticsTrackFileDescriptorSupplier
      extends LogisticsTrackBaseDescriptorSupplier {
    LogisticsTrackFileDescriptorSupplier() {}
  }

  private static final class LogisticsTrackMethodDescriptorSupplier
      extends LogisticsTrackBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    LogisticsTrackMethodDescriptorSupplier(String methodName) {
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
      synchronized (LogisticsTrackGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new LogisticsTrackFileDescriptorSupplier())
              .addMethod(getGetLogisticsTrackMethod())
              .build();
        }
      }
    }
    return result;
  }
}
