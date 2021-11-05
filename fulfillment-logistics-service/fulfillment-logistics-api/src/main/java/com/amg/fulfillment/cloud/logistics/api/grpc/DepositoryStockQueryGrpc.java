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
    comments = "Source: depositoryStockDetail.proto")
public final class DepositoryStockQueryGrpc {

  private DepositoryStockQueryGrpc() {}

  public static final String SERVICE_NAME = "DepositoryStockQuery";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult> getQuerySaleOrderStautsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "querySaleOrderStauts",
      requestType = com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest.class,
      responseType = com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest,
      com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult> getQuerySaleOrderStautsMethod() {
    io.grpc.MethodDescriptor<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest, com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult> getQuerySaleOrderStautsMethod;
    if ((getQuerySaleOrderStautsMethod = DepositoryStockQueryGrpc.getQuerySaleOrderStautsMethod) == null) {
      synchronized (DepositoryStockQueryGrpc.class) {
        if ((getQuerySaleOrderStautsMethod = DepositoryStockQueryGrpc.getQuerySaleOrderStautsMethod) == null) {
          DepositoryStockQueryGrpc.getQuerySaleOrderStautsMethod = getQuerySaleOrderStautsMethod = 
              io.grpc.MethodDescriptor.<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest, com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "DepositoryStockQuery", "querySaleOrderStauts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new DepositoryStockQueryMethodDescriptorSupplier("querySaleOrderStauts"))
                  .build();
          }
        }
     }
     return getQuerySaleOrderStautsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DepositoryStockQueryStub newStub(io.grpc.Channel channel) {
    return new DepositoryStockQueryStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DepositoryStockQueryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DepositoryStockQueryBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DepositoryStockQueryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DepositoryStockQueryFutureStub(channel);
  }

  /**
   */
  public static abstract class DepositoryStockQueryImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponse> querySaleOrderStauts(com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getQuerySaleOrderStautsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest,
                com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult>(
                  this, METHODID_QUERY_SALE_ORDER_STAUTS)))
          .build();
    }
  }

  /**
   */
  public static final class DepositoryStockQueryStub extends io.grpc.stub.AbstractStub<DepositoryStockQueryStub> {
    private DepositoryStockQueryStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DepositoryStockQueryStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DepositoryStockQueryStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DepositoryStockQueryStub(channel, callOptions);
    }

    /**
     */
    public void querySaleOrderStauts(com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest request,
        io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getQuerySaleOrderStautsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DepositoryStockQueryBlockingStub extends io.grpc.stub.AbstractStub<DepositoryStockQueryBlockingStub> {
    private DepositoryStockQueryBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DepositoryStockQueryBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DepositoryStockQueryBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DepositoryStockQueryBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult querySaleOrderStauts(com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest request) {
      return blockingUnaryCall(
          getChannel(), getQuerySaleOrderStautsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DepositoryStockQueryFutureStub extends io.grpc.stub.AbstractStub<DepositoryStockQueryFutureStub> {
    private DepositoryStockQueryFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DepositoryStockQueryFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DepositoryStockQueryFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DepositoryStockQueryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult> querySaleOrderStauts(
        com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getQuerySaleOrderStautsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QUERY_SALE_ORDER_STAUTS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DepositoryStockQueryImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DepositoryStockQueryImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY_SALE_ORDER_STAUTS:
          serviceImpl.querySaleOrderStauts((com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailRequest) request,
              (io.grpc.stub.StreamObserver<com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.DepositoryStockDetailResponseResult>) responseObserver);
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

  private static abstract class DepositoryStockQueryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DepositoryStockQueryBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.amg.fulfillment.cloud.logistics.api.proto.DepositoryStockGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DepositoryStockQuery");
    }
  }

  private static final class DepositoryStockQueryFileDescriptorSupplier
      extends DepositoryStockQueryBaseDescriptorSupplier {
    DepositoryStockQueryFileDescriptorSupplier() {}
  }

  private static final class DepositoryStockQueryMethodDescriptorSupplier
      extends DepositoryStockQueryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DepositoryStockQueryMethodDescriptorSupplier(String methodName) {
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
      synchronized (DepositoryStockQueryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DepositoryStockQueryFileDescriptorSupplier())
              .addMethod(getQuerySaleOrderStautsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
