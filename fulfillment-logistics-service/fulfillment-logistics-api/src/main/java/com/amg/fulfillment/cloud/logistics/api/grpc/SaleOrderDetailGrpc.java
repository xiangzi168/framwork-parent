package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderDetailGTO;

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
    comments = "Source: saleOrderDetail.proto")
public final class SaleOrderDetailGrpc {

  private SaleOrderDetailGrpc() {}

  public static final String SERVICE_NAME = "SaleOrderDetail";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SaleOrderDetailGTO.SaleOrderDetailRequest,
      SaleOrderDetailGTO.SaleOrderResponseResult> getAddSaleOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "addSaleOrder",
      requestType = SaleOrderDetailGTO.SaleOrderDetailRequest.class,
      responseType = SaleOrderDetailGTO.SaleOrderResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SaleOrderDetailGTO.SaleOrderDetailRequest,
      SaleOrderDetailGTO.SaleOrderResponseResult> getAddSaleOrderMethod() {
    io.grpc.MethodDescriptor<SaleOrderDetailGTO.SaleOrderDetailRequest, SaleOrderDetailGTO.SaleOrderResponseResult> getAddSaleOrderMethod;
    if ((getAddSaleOrderMethod = SaleOrderDetailGrpc.getAddSaleOrderMethod) == null) {
      synchronized (SaleOrderDetailGrpc.class) {
        if ((getAddSaleOrderMethod = SaleOrderDetailGrpc.getAddSaleOrderMethod) == null) {
          SaleOrderDetailGrpc.getAddSaleOrderMethod = getAddSaleOrderMethod = 
              io.grpc.MethodDescriptor.<SaleOrderDetailGTO.SaleOrderDetailRequest, SaleOrderDetailGTO.SaleOrderResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SaleOrderDetail", "addSaleOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderDetailGTO.SaleOrderDetailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderDetailGTO.SaleOrderResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SaleOrderDetailMethodDescriptorSupplier("addSaleOrder"))
                  .build();
          }
        }
     }
     return getAddSaleOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SaleOrderDetailStub newStub(io.grpc.Channel channel) {
    return new SaleOrderDetailStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SaleOrderDetailBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SaleOrderDetailBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SaleOrderDetailFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SaleOrderDetailFutureStub(channel);
  }

  /**
   */
  public static abstract class SaleOrderDetailImplBase implements io.grpc.BindableService {

    /**
     */
    public com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderDetailGTO.SaleOrderResponseDetail addSaleOrder(SaleOrderDetailGTO.SaleOrderDetailRequest request,
                             io.grpc.stub.StreamObserver<SaleOrderDetailGTO.SaleOrderResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAddSaleOrderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SaleOrderDetailGTO.SaleOrderDetailRequest,
                SaleOrderDetailGTO.SaleOrderResponseResult>(
                  this, METHODID_ADD_SALE_ORDER)))
          .build();
    }
  }

  /**
   */
  public static final class SaleOrderDetailStub extends io.grpc.stub.AbstractStub<SaleOrderDetailStub> {
    private SaleOrderDetailStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderDetailStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderDetailStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderDetailStub(channel, callOptions);
    }

    /**
     */
    public void addSaleOrder(SaleOrderDetailGTO.SaleOrderDetailRequest request,
                             io.grpc.stub.StreamObserver<SaleOrderDetailGTO.SaleOrderResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAddSaleOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SaleOrderDetailBlockingStub extends io.grpc.stub.AbstractStub<SaleOrderDetailBlockingStub> {
    private SaleOrderDetailBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderDetailBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderDetailBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderDetailBlockingStub(channel, callOptions);
    }

    /**
     */
    public SaleOrderDetailGTO.SaleOrderResponseResult addSaleOrder(SaleOrderDetailGTO.SaleOrderDetailRequest request) {
      return blockingUnaryCall(
          getChannel(), getAddSaleOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SaleOrderDetailFutureStub extends io.grpc.stub.AbstractStub<SaleOrderDetailFutureStub> {
    private SaleOrderDetailFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderDetailFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderDetailFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderDetailFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SaleOrderDetailGTO.SaleOrderResponseResult> addSaleOrder(
        SaleOrderDetailGTO.SaleOrderDetailRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAddSaleOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ADD_SALE_ORDER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SaleOrderDetailImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SaleOrderDetailImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ADD_SALE_ORDER:
          serviceImpl.addSaleOrder((SaleOrderDetailGTO.SaleOrderDetailRequest) request,
              (io.grpc.stub.StreamObserver<SaleOrderDetailGTO.SaleOrderResponseResult>) responseObserver);
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

  private static abstract class SaleOrderDetailBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SaleOrderDetailBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SaleOrderDetailGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SaleOrderDetail");
    }
  }

  private static final class SaleOrderDetailFileDescriptorSupplier
      extends SaleOrderDetailBaseDescriptorSupplier {
    SaleOrderDetailFileDescriptorSupplier() {}
  }

  private static final class SaleOrderDetailMethodDescriptorSupplier
      extends SaleOrderDetailBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SaleOrderDetailMethodDescriptorSupplier(String methodName) {
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
      synchronized (SaleOrderDetailGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SaleOrderDetailFileDescriptorSupplier())
              .addMethod(getAddSaleOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
