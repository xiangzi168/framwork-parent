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
    comments = "Source: manualOutDepository.proto")
public final class ManualOutDepositoryGrpc {

  private ManualOutDepositoryGrpc() {}

  public static final String SERVICE_NAME = "ManualOutDepository";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> getAddOutDepositoryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "addOutDepository",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> getAddOutDepositoryMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest, com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> getAddOutDepositoryMethod;
    if ((getAddOutDepositoryMethod = ManualOutDepositoryGrpc.getAddOutDepositoryMethod) == null) {
      synchronized (ManualOutDepositoryGrpc.class) {
        if ((getAddOutDepositoryMethod = ManualOutDepositoryGrpc.getAddOutDepositoryMethod) == null) {
          ManualOutDepositoryGrpc.getAddOutDepositoryMethod = getAddOutDepositoryMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest, com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ManualOutDepository", "addOutDepository"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ManualOutDepositoryMethodDescriptorSupplier("addOutDepository"))
                  .build();
          }
        }
     }
     return getAddOutDepositoryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ManualOutDepositoryStub newStub(io.grpc.Channel channel) {
    return new ManualOutDepositoryStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ManualOutDepositoryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ManualOutDepositoryBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ManualOutDepositoryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ManualOutDepositoryFutureStub(channel);
  }

  /**
   */
  public static abstract class ManualOutDepositoryImplBase implements io.grpc.BindableService {

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseDetail addOutDepository(com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAddOutDepositoryMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult>(
                  this, METHODID_ADD_OUT_DEPOSITORY)))
          .build();
    }
  }

  /**
   */
  public static final class ManualOutDepositoryStub extends io.grpc.stub.AbstractStub<ManualOutDepositoryStub> {
    private ManualOutDepositoryStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ManualOutDepositoryStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ManualOutDepositoryStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ManualOutDepositoryStub(channel, callOptions);
    }

    /**
     */
    public void addOutDepository(com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAddOutDepositoryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ManualOutDepositoryBlockingStub extends io.grpc.stub.AbstractStub<ManualOutDepositoryBlockingStub> {
    private ManualOutDepositoryBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ManualOutDepositoryBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ManualOutDepositoryBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ManualOutDepositoryBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult addOutDepository(com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest request) {
      return blockingUnaryCall(
          getChannel(), getAddOutDepositoryMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ManualOutDepositoryFutureStub extends io.grpc.stub.AbstractStub<ManualOutDepositoryFutureStub> {
    private ManualOutDepositoryFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ManualOutDepositoryFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ManualOutDepositoryFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ManualOutDepositoryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult> addOutDepository(
        com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAddOutDepositoryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_OUT_DEPOSITORY = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ManualOutDepositoryImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ManualOutDepositoryImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_OUT_DEPOSITORY:
          serviceImpl.addOutDepository((com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.ManualOutDepositoryResponseResult>) responseObserver);
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

  private static abstract class ManualOutDepositoryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ManualOutDepositoryBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.ManualOutDepositoryGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ManualOutDepository");
    }
  }

  private static final class ManualOutDepositoryFileDescriptorSupplier
      extends ManualOutDepositoryBaseDescriptorSupplier {
    ManualOutDepositoryFileDescriptorSupplier() {}
  }

  private static final class ManualOutDepositoryMethodDescriptorSupplier
      extends ManualOutDepositoryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ManualOutDepositoryMethodDescriptorSupplier(String methodName) {
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
      synchronized (ManualOutDepositoryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ManualOutDepositoryFileDescriptorSupplier())
              .addMethod(getAddOutDepositoryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
