package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderStatusQueryGTO;

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
    comments = "Source: saleOrderStatusQuery.proto")
public final class SaleOrderStatusQueryGrpc {

  private SaleOrderStatusQueryGrpc() {}

  public static final String SERVICE_NAME = "SaleOrderStatusQuery";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest,
      SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> getQuerySaleOrderStautsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "querySaleOrderStauts",
      requestType = SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest.class,
      responseType = SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest,
      SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> getQuerySaleOrderStautsMethod() {
    io.grpc.MethodDescriptor<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest, SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> getQuerySaleOrderStautsMethod;
    if ((getQuerySaleOrderStautsMethod = SaleOrderStatusQueryGrpc.getQuerySaleOrderStautsMethod) == null) {
      synchronized (SaleOrderStatusQueryGrpc.class) {
        if ((getQuerySaleOrderStautsMethod = SaleOrderStatusQueryGrpc.getQuerySaleOrderStautsMethod) == null) {
          SaleOrderStatusQueryGrpc.getQuerySaleOrderStautsMethod = getQuerySaleOrderStautsMethod = 
              io.grpc.MethodDescriptor.<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest, SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SaleOrderStatusQuery", "querySaleOrderStauts"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SaleOrderStatusQueryMethodDescriptorSupplier("querySaleOrderStauts"))
                  .build();
          }
        }
     }
     return getQuerySaleOrderStautsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest,
      SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> getQuerySaleOrderStautsForReportMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "querySaleOrderStautsForReport",
      requestType = SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest.class,
      responseType = SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest,
      SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> getQuerySaleOrderStautsForReportMethod() {
    io.grpc.MethodDescriptor<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest, SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> getQuerySaleOrderStautsForReportMethod;
    if ((getQuerySaleOrderStautsForReportMethod = SaleOrderStatusQueryGrpc.getQuerySaleOrderStautsForReportMethod) == null) {
      synchronized (SaleOrderStatusQueryGrpc.class) {
        if ((getQuerySaleOrderStautsForReportMethod = SaleOrderStatusQueryGrpc.getQuerySaleOrderStautsForReportMethod) == null) {
          SaleOrderStatusQueryGrpc.getQuerySaleOrderStautsForReportMethod = getQuerySaleOrderStautsForReportMethod = 
              io.grpc.MethodDescriptor.<SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest, SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SaleOrderStatusQuery", "querySaleOrderStautsForReport"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SaleOrderStatusQueryMethodDescriptorSupplier("querySaleOrderStautsForReport"))
                  .build();
          }
        }
     }
     return getQuerySaleOrderStautsForReportMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SaleOrderStatusQueryStub newStub(io.grpc.Channel channel) {
    return new SaleOrderStatusQueryStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SaleOrderStatusQueryBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SaleOrderStatusQueryBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SaleOrderStatusQueryFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SaleOrderStatusQueryFutureStub(channel);
  }

  /**
   */
  public static abstract class SaleOrderStatusQueryImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> querySaleOrderStauts(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request,
                                     io.grpc.stub.StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponse> querySaleOrderStautsForReport(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request,
                                              io.grpc.stub.StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getQuerySaleOrderStautsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest,
                SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult>(
                  this, METHODID_QUERY_SALE_ORDER_STAUTS)))
          .addMethod(
            getQuerySaleOrderStautsForReportMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest,
                SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult>(
                  this, METHODID_QUERY_SALE_ORDER_STAUTS_FOR_REPORT)))
          .build();
    }
  }

  /**
   */
  public static final class SaleOrderStatusQueryStub extends io.grpc.stub.AbstractStub<SaleOrderStatusQueryStub> {
    private SaleOrderStatusQueryStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderStatusQueryStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderStatusQueryStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderStatusQueryStub(channel, callOptions);
    }

    /**
     */
    public void querySaleOrderStauts(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request,
                                     io.grpc.stub.StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getQuerySaleOrderStautsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void querySaleOrderStautsForReport(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request,
                                              io.grpc.stub.StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getQuerySaleOrderStautsForReportMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SaleOrderStatusQueryBlockingStub extends io.grpc.stub.AbstractStub<SaleOrderStatusQueryBlockingStub> {
    private SaleOrderStatusQueryBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderStatusQueryBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderStatusQueryBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderStatusQueryBlockingStub(channel, callOptions);
    }

    /**
     */
    public SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult querySaleOrderStauts(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request) {
      return blockingUnaryCall(
          getChannel(), getQuerySaleOrderStautsMethod(), getCallOptions(), request);
    }

    /**
     */
    public SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult querySaleOrderStautsForReport(SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request) {
      return blockingUnaryCall(
          getChannel(), getQuerySaleOrderStautsForReportMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SaleOrderStatusQueryFutureStub extends io.grpc.stub.AbstractStub<SaleOrderStatusQueryFutureStub> {
    private SaleOrderStatusQueryFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderStatusQueryFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderStatusQueryFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderStatusQueryFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> querySaleOrderStauts(
        SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getQuerySaleOrderStautsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult> querySaleOrderStautsForReport(
        SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getQuerySaleOrderStautsForReportMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QUERY_SALE_ORDER_STAUTS = 0;
  private static final int METHODID_QUERY_SALE_ORDER_STAUTS_FOR_REPORT = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SaleOrderStatusQueryImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SaleOrderStatusQueryImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY_SALE_ORDER_STAUTS:
          serviceImpl.querySaleOrderStauts((SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest) request,
              (io.grpc.stub.StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult>) responseObserver);
          break;
        case METHODID_QUERY_SALE_ORDER_STAUTS_FOR_REPORT:
          serviceImpl.querySaleOrderStautsForReport((SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest) request,
              (io.grpc.stub.StreamObserver<SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult>) responseObserver);
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

  private static abstract class SaleOrderStatusQueryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SaleOrderStatusQueryBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SaleOrderStatusQueryGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SaleOrderStatusQuery");
    }
  }

  private static final class SaleOrderStatusQueryFileDescriptorSupplier
      extends SaleOrderStatusQueryBaseDescriptorSupplier {
    SaleOrderStatusQueryFileDescriptorSupplier() {}
  }

  private static final class SaleOrderStatusQueryMethodDescriptorSupplier
      extends SaleOrderStatusQueryBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SaleOrderStatusQueryMethodDescriptorSupplier(String methodName) {
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
      synchronized (SaleOrderStatusQueryGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SaleOrderStatusQueryFileDescriptorSupplier())
              .addMethod(getQuerySaleOrderStautsMethod())
              .addMethod(getQuerySaleOrderStautsForReportMethod())
              .build();
        }
      }
    }
    return result;
  }
}
