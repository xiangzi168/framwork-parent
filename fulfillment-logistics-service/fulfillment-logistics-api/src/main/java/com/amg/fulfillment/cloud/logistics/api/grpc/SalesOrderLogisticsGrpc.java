package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.SalesOrderLogisticsGTO;

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
    comments = "Source: salesOrderLogistics.proto")
public final class SalesOrderLogisticsGrpc {

  private SalesOrderLogisticsGrpc() {}

  public static final String SERVICE_NAME = "SalesOrderLogistics";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest,
      SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> getGetSalesOrderLogisticsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getSalesOrderLogistics",
      requestType = SalesOrderLogisticsGTO.SalesOrderLogisticsRequest.class,
      responseType = SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest,
      SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> getGetSalesOrderLogisticsMethod() {
    io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest, SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> getGetSalesOrderLogisticsMethod;
    if ((getGetSalesOrderLogisticsMethod = SalesOrderLogisticsGrpc.getGetSalesOrderLogisticsMethod) == null) {
      synchronized (SalesOrderLogisticsGrpc.class) {
        if ((getGetSalesOrderLogisticsMethod = SalesOrderLogisticsGrpc.getGetSalesOrderLogisticsMethod) == null) {
          SalesOrderLogisticsGrpc.getGetSalesOrderLogisticsMethod = getGetSalesOrderLogisticsMethod = 
              io.grpc.MethodDescriptor.<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest, SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SalesOrderLogistics", "getSalesOrderLogistics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SalesOrderLogisticsGTO.SalesOrderLogisticsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SalesOrderLogisticsMethodDescriptorSupplier("getSalesOrderLogistics"))
                  .build();
          }
        }
     }
     return getGetSalesOrderLogisticsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest,
      SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> getGetSalesOrderLogisticsListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getSalesOrderLogisticsList",
      requestType = SalesOrderLogisticsGTO.SalesOrderLogisticsRequest.class,
      responseType = SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest,
      SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> getGetSalesOrderLogisticsListMethod() {
    io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest, SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> getGetSalesOrderLogisticsListMethod;
    if ((getGetSalesOrderLogisticsListMethod = SalesOrderLogisticsGrpc.getGetSalesOrderLogisticsListMethod) == null) {
      synchronized (SalesOrderLogisticsGrpc.class) {
        if ((getGetSalesOrderLogisticsListMethod = SalesOrderLogisticsGrpc.getGetSalesOrderLogisticsListMethod) == null) {
          SalesOrderLogisticsGrpc.getGetSalesOrderLogisticsListMethod = getGetSalesOrderLogisticsListMethod = 
              io.grpc.MethodDescriptor.<SalesOrderLogisticsGTO.SalesOrderLogisticsRequest, SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SalesOrderLogistics", "getSalesOrderLogisticsList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SalesOrderLogisticsGTO.SalesOrderLogisticsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SalesOrderLogisticsMethodDescriptorSupplier("getSalesOrderLogisticsList"))
                  .build();
          }
        }
     }
     return getGetSalesOrderLogisticsListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderPackageIdRequest,
      SalesOrderLogisticsGTO.SalesOrderIdResponseResult> getGetSalesOrderIdByPackageIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getSalesOrderIdByPackageId",
      requestType = SalesOrderLogisticsGTO.SalesOrderPackageIdRequest.class,
      responseType = SalesOrderLogisticsGTO.SalesOrderIdResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderPackageIdRequest,
      SalesOrderLogisticsGTO.SalesOrderIdResponseResult> getGetSalesOrderIdByPackageIdMethod() {
    io.grpc.MethodDescriptor<SalesOrderLogisticsGTO.SalesOrderPackageIdRequest, SalesOrderLogisticsGTO.SalesOrderIdResponseResult> getGetSalesOrderIdByPackageIdMethod;
    if ((getGetSalesOrderIdByPackageIdMethod = SalesOrderLogisticsGrpc.getGetSalesOrderIdByPackageIdMethod) == null) {
      synchronized (SalesOrderLogisticsGrpc.class) {
        if ((getGetSalesOrderIdByPackageIdMethod = SalesOrderLogisticsGrpc.getGetSalesOrderIdByPackageIdMethod) == null) {
          SalesOrderLogisticsGrpc.getGetSalesOrderIdByPackageIdMethod = getGetSalesOrderIdByPackageIdMethod = 
              io.grpc.MethodDescriptor.<SalesOrderLogisticsGTO.SalesOrderPackageIdRequest, SalesOrderLogisticsGTO.SalesOrderIdResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SalesOrderLogistics", "getSalesOrderIdByPackageId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SalesOrderLogisticsGTO.SalesOrderPackageIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SalesOrderLogisticsGTO.SalesOrderIdResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SalesOrderLogisticsMethodDescriptorSupplier("getSalesOrderIdByPackageId"))
                  .build();
          }
        }
     }
     return getGetSalesOrderIdByPackageIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SalesOrderLogisticsStub newStub(io.grpc.Channel channel) {
    return new SalesOrderLogisticsStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SalesOrderLogisticsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SalesOrderLogisticsBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SalesOrderLogisticsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SalesOrderLogisticsFutureStub(channel);
  }

  /**
   */
  public static abstract class SalesOrderLogisticsImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> getSalesOrderLogistics(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request,
                                       io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse> getSalesOrderLogisticsList(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request,
                                           io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<String> getSalesOrderIdByPackageId(SalesOrderLogisticsGTO.SalesOrderPackageIdRequest request,
                                           io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderIdResponseResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetSalesOrderLogisticsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SalesOrderLogisticsGTO.SalesOrderLogisticsRequest,
                SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult>(
                  this, METHODID_GET_SALES_ORDER_LOGISTICS)))
          .addMethod(
            getGetSalesOrderLogisticsListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SalesOrderLogisticsGTO.SalesOrderLogisticsRequest,
                SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult>(
                  this, METHODID_GET_SALES_ORDER_LOGISTICS_LIST)))
          .addMethod(
            getGetSalesOrderIdByPackageIdMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SalesOrderLogisticsGTO.SalesOrderPackageIdRequest,
                SalesOrderLogisticsGTO.SalesOrderIdResponseResult>(
                  this, METHODID_GET_SALES_ORDER_ID_BY_PACKAGE_ID)))
          .build();
    }
  }

  /**
   */
  public static final class SalesOrderLogisticsStub extends io.grpc.stub.AbstractStub<SalesOrderLogisticsStub> {
    private SalesOrderLogisticsStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SalesOrderLogisticsStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SalesOrderLogisticsStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SalesOrderLogisticsStub(channel, callOptions);
    }

    /**
     */
    public void getSalesOrderLogistics(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request,
                                       io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSalesOrderLogisticsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSalesOrderLogisticsList(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request,
                                           io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSalesOrderLogisticsListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSalesOrderIdByPackageId(SalesOrderLogisticsGTO.SalesOrderPackageIdRequest request,
                                           io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderIdResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSalesOrderIdByPackageIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SalesOrderLogisticsBlockingStub extends io.grpc.stub.AbstractStub<SalesOrderLogisticsBlockingStub> {
    private SalesOrderLogisticsBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SalesOrderLogisticsBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SalesOrderLogisticsBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SalesOrderLogisticsBlockingStub(channel, callOptions);
    }

    /**
     */
    public SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult getSalesOrderLogistics(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetSalesOrderLogisticsMethod(), getCallOptions(), request);
    }

    /**
     */
    public SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult getSalesOrderLogisticsList(SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetSalesOrderLogisticsListMethod(), getCallOptions(), request);
    }

    /**
     */
    public SalesOrderLogisticsGTO.SalesOrderIdResponseResult getSalesOrderIdByPackageId(SalesOrderLogisticsGTO.SalesOrderPackageIdRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetSalesOrderIdByPackageIdMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SalesOrderLogisticsFutureStub extends io.grpc.stub.AbstractStub<SalesOrderLogisticsFutureStub> {
    private SalesOrderLogisticsFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SalesOrderLogisticsFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SalesOrderLogisticsFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SalesOrderLogisticsFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult> getSalesOrderLogistics(
        SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSalesOrderLogisticsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult> getSalesOrderLogisticsList(
        SalesOrderLogisticsGTO.SalesOrderLogisticsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSalesOrderLogisticsListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SalesOrderLogisticsGTO.SalesOrderIdResponseResult> getSalesOrderIdByPackageId(
        SalesOrderLogisticsGTO.SalesOrderPackageIdRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSalesOrderIdByPackageIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_SALES_ORDER_LOGISTICS = 0;
  private static final int METHODID_GET_SALES_ORDER_LOGISTICS_LIST = 1;
  private static final int METHODID_GET_SALES_ORDER_ID_BY_PACKAGE_ID = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SalesOrderLogisticsImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SalesOrderLogisticsImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_SALES_ORDER_LOGISTICS:
          serviceImpl.getSalesOrderLogistics((SalesOrderLogisticsGTO.SalesOrderLogisticsRequest) request,
              (io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsResponseResult>) responseObserver);
          break;
        case METHODID_GET_SALES_ORDER_LOGISTICS_LIST:
          serviceImpl.getSalesOrderLogisticsList((SalesOrderLogisticsGTO.SalesOrderLogisticsRequest) request,
              (io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponseResult>) responseObserver);
          break;
        case METHODID_GET_SALES_ORDER_ID_BY_PACKAGE_ID:
          serviceImpl.getSalesOrderIdByPackageId((SalesOrderLogisticsGTO.SalesOrderPackageIdRequest) request,
              (io.grpc.stub.StreamObserver<SalesOrderLogisticsGTO.SalesOrderIdResponseResult>) responseObserver);
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

  private static abstract class SalesOrderLogisticsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SalesOrderLogisticsBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SalesOrderLogisticsGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SalesOrderLogistics");
    }
  }

  private static final class SalesOrderLogisticsFileDescriptorSupplier
      extends SalesOrderLogisticsBaseDescriptorSupplier {
    SalesOrderLogisticsFileDescriptorSupplier() {}
  }

  private static final class SalesOrderLogisticsMethodDescriptorSupplier
      extends SalesOrderLogisticsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SalesOrderLogisticsMethodDescriptorSupplier(String methodName) {
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
      synchronized (SalesOrderLogisticsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SalesOrderLogisticsFileDescriptorSupplier())
              .addMethod(getGetSalesOrderLogisticsMethod())
              .addMethod(getGetSalesOrderLogisticsListMethod())
              .addMethod(getGetSalesOrderIdByPackageIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}
