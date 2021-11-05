package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO;

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
    comments = "Source: channelOrderLogistics.proto")
public final class ChannelOrderLogisticsGrpc {

  private ChannelOrderLogisticsGrpc() {}

  public static final String SERVICE_NAME = "ChannelOrderLogistics";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest,
      ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> getGetChannelOrderLogisticsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getChannelOrderLogistics",
      requestType = ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest.class,
      responseType = ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest,
      ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> getGetChannelOrderLogisticsMethod() {
    io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest, ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> getGetChannelOrderLogisticsMethod;
    if ((getGetChannelOrderLogisticsMethod = ChannelOrderLogisticsGrpc.getGetChannelOrderLogisticsMethod) == null) {
      synchronized (ChannelOrderLogisticsGrpc.class) {
        if ((getGetChannelOrderLogisticsMethod = ChannelOrderLogisticsGrpc.getGetChannelOrderLogisticsMethod) == null) {
          ChannelOrderLogisticsGrpc.getGetChannelOrderLogisticsMethod = getGetChannelOrderLogisticsMethod = 
              io.grpc.MethodDescriptor.<ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest, ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderLogistics", "getChannelOrderLogistics"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderLogisticsMethodDescriptorSupplier("getChannelOrderLogistics"))
                  .build();
          }
        }
     }
     return getGetChannelOrderLogisticsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest,
      ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> getGetChannerOrderIdByPackageIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getChannerOrderIdByPackageId",
      requestType = ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest.class,
      responseType = ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest,
      ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> getGetChannerOrderIdByPackageIdMethod() {
    io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest, ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> getGetChannerOrderIdByPackageIdMethod;
    if ((getGetChannerOrderIdByPackageIdMethod = ChannelOrderLogisticsGrpc.getGetChannerOrderIdByPackageIdMethod) == null) {
      synchronized (ChannelOrderLogisticsGrpc.class) {
        if ((getGetChannerOrderIdByPackageIdMethod = ChannelOrderLogisticsGrpc.getGetChannerOrderIdByPackageIdMethod) == null) {
          ChannelOrderLogisticsGrpc.getGetChannerOrderIdByPackageIdMethod = getGetChannerOrderIdByPackageIdMethod = 
              io.grpc.MethodDescriptor.<ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest, ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderLogistics", "getChannerOrderIdByPackageId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderLogisticsMethodDescriptorSupplier("getChannerOrderIdByPackageId"))
                  .build();
          }
        }
     }
     return getGetChannerOrderIdByPackageIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.updateChannelOrderIdRequest,
      ChannelOrderLogisticsGTO.updateChannelOrderIdResult> getUpdateChannelOrderIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updateChannelOrderId",
      requestType = ChannelOrderLogisticsGTO.updateChannelOrderIdRequest.class,
      responseType = ChannelOrderLogisticsGTO.updateChannelOrderIdResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.updateChannelOrderIdRequest,
      ChannelOrderLogisticsGTO.updateChannelOrderIdResult> getUpdateChannelOrderIdMethod() {
    io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.updateChannelOrderIdRequest, ChannelOrderLogisticsGTO.updateChannelOrderIdResult> getUpdateChannelOrderIdMethod;
    if ((getUpdateChannelOrderIdMethod = ChannelOrderLogisticsGrpc.getUpdateChannelOrderIdMethod) == null) {
      synchronized (ChannelOrderLogisticsGrpc.class) {
        if ((getUpdateChannelOrderIdMethod = ChannelOrderLogisticsGrpc.getUpdateChannelOrderIdMethod) == null) {
          ChannelOrderLogisticsGrpc.getUpdateChannelOrderIdMethod = getUpdateChannelOrderIdMethod = 
              io.grpc.MethodDescriptor.<ChannelOrderLogisticsGTO.updateChannelOrderIdRequest, ChannelOrderLogisticsGTO.updateChannelOrderIdResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderLogistics", "updateChannelOrderId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.updateChannelOrderIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.updateChannelOrderIdResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderLogisticsMethodDescriptorSupplier("updateChannelOrderId"))
                  .build();
          }
        }
     }
     return getUpdateChannelOrderIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.AddPurchaseOrderRequest,
      ChannelOrderLogisticsGTO.AddPurchaseOrderResult> getAddPurchaseOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "addPurchaseOrder",
      requestType = ChannelOrderLogisticsGTO.AddPurchaseOrderRequest.class,
      responseType = ChannelOrderLogisticsGTO.AddPurchaseOrderResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.AddPurchaseOrderRequest,
      ChannelOrderLogisticsGTO.AddPurchaseOrderResult> getAddPurchaseOrderMethod() {
    io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.AddPurchaseOrderRequest, ChannelOrderLogisticsGTO.AddPurchaseOrderResult> getAddPurchaseOrderMethod;
    if ((getAddPurchaseOrderMethod = ChannelOrderLogisticsGrpc.getAddPurchaseOrderMethod) == null) {
      synchronized (ChannelOrderLogisticsGrpc.class) {
        if ((getAddPurchaseOrderMethod = ChannelOrderLogisticsGrpc.getAddPurchaseOrderMethod) == null) {
          ChannelOrderLogisticsGrpc.getAddPurchaseOrderMethod = getAddPurchaseOrderMethod = 
              io.grpc.MethodDescriptor.<ChannelOrderLogisticsGTO.AddPurchaseOrderRequest, ChannelOrderLogisticsGTO.AddPurchaseOrderResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderLogistics", "addPurchaseOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.AddPurchaseOrderRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.AddPurchaseOrderResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderLogisticsMethodDescriptorSupplier("addPurchaseOrder"))
                  .build();
          }
        }
     }
     return getAddPurchaseOrderMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest,
      ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> getUpdatePurchasePackageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updatePurchasePackage",
      requestType = ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest.class,
      responseType = ChannelOrderLogisticsGTO.UpdatePurchasePackageResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest,
      ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> getUpdatePurchasePackageMethod() {
    io.grpc.MethodDescriptor<ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest, ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> getUpdatePurchasePackageMethod;
    if ((getUpdatePurchasePackageMethod = ChannelOrderLogisticsGrpc.getUpdatePurchasePackageMethod) == null) {
      synchronized (ChannelOrderLogisticsGrpc.class) {
        if ((getUpdatePurchasePackageMethod = ChannelOrderLogisticsGrpc.getUpdatePurchasePackageMethod) == null) {
          ChannelOrderLogisticsGrpc.getUpdatePurchasePackageMethod = getUpdatePurchasePackageMethod = 
              io.grpc.MethodDescriptor.<ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest, ChannelOrderLogisticsGTO.UpdatePurchasePackageResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "ChannelOrderLogistics", "updatePurchasePackage"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ChannelOrderLogisticsGTO.UpdatePurchasePackageResult.getDefaultInstance()))
                  .setSchemaDescriptor(new ChannelOrderLogisticsMethodDescriptorSupplier("updatePurchasePackage"))
                  .build();
          }
        }
     }
     return getUpdatePurchasePackageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ChannelOrderLogisticsStub newStub(io.grpc.Channel channel) {
    return new ChannelOrderLogisticsStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ChannelOrderLogisticsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ChannelOrderLogisticsBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ChannelOrderLogisticsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ChannelOrderLogisticsFutureStub(channel);
  }

  /**
   */
  public static abstract class ChannelOrderLogisticsImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> getChannelOrderLogistics(ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request,
                                         io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<String> getChannerOrderIdByPackageId(ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest request,
                                             io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.lang.Boolean updateChannelOrderId(ChannelOrderLogisticsGTO.updateChannelOrderIdRequest request,
                                     io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.updateChannelOrderIdResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO.AddPurchaseOrderResponse> addPurchaseOrder(ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request,
                                 io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.AddPurchaseOrderResult> responseObserver) {
			return null;
    }

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO.UpdatePurchasePackageResponse> updatePurchasePackage(ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request,
                                      io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetChannelOrderLogisticsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest,
                ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult>(
                  this, METHODID_GET_CHANNEL_ORDER_LOGISTICS)))
          .addMethod(
            getGetChannerOrderIdByPackageIdMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest,
                ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult>(
                  this, METHODID_GET_CHANNER_ORDER_ID_BY_PACKAGE_ID)))
          .addMethod(
            getUpdateChannelOrderIdMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ChannelOrderLogisticsGTO.updateChannelOrderIdRequest,
                ChannelOrderLogisticsGTO.updateChannelOrderIdResult>(
                  this, METHODID_UPDATE_CHANNEL_ORDER_ID)))
          .addMethod(
            getAddPurchaseOrderMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ChannelOrderLogisticsGTO.AddPurchaseOrderRequest,
                ChannelOrderLogisticsGTO.AddPurchaseOrderResult>(
                  this, METHODID_ADD_PURCHASE_ORDER)))
          .addMethod(
            getUpdatePurchasePackageMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest,
                ChannelOrderLogisticsGTO.UpdatePurchasePackageResult>(
                  this, METHODID_UPDATE_PURCHASE_PACKAGE)))
          .build();
    }
  }

  /**
   */
  public static final class ChannelOrderLogisticsStub extends io.grpc.stub.AbstractStub<ChannelOrderLogisticsStub> {
    private ChannelOrderLogisticsStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChannelOrderLogisticsStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ChannelOrderLogisticsStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChannelOrderLogisticsStub(channel, callOptions);
    }

    /**
     */
    public void getChannelOrderLogistics(ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request,
                                         io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetChannelOrderLogisticsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getChannerOrderIdByPackageId(ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest request,
                                             io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetChannerOrderIdByPackageIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateChannelOrderId(ChannelOrderLogisticsGTO.updateChannelOrderIdRequest request,
                                     io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.updateChannelOrderIdResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateChannelOrderIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void addPurchaseOrder(ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request,
                                 io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.AddPurchaseOrderResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAddPurchaseOrderMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updatePurchasePackage(ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request,
                                      io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdatePurchasePackageMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ChannelOrderLogisticsBlockingStub extends io.grpc.stub.AbstractStub<ChannelOrderLogisticsBlockingStub> {
    private ChannelOrderLogisticsBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChannelOrderLogisticsBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ChannelOrderLogisticsBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChannelOrderLogisticsBlockingStub(channel, callOptions);
    }

    /**
     */
    public ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult getChannelOrderLogistics(ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetChannelOrderLogisticsMethod(), getCallOptions(), request);
    }

    /**
     */
    public ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult getChannerOrderIdByPackageId(ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetChannerOrderIdByPackageIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public ChannelOrderLogisticsGTO.updateChannelOrderIdResult updateChannelOrderId(ChannelOrderLogisticsGTO.updateChannelOrderIdRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateChannelOrderIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public ChannelOrderLogisticsGTO.AddPurchaseOrderResult addPurchaseOrder(ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request) {
      return blockingUnaryCall(
          getChannel(), getAddPurchaseOrderMethod(), getCallOptions(), request);
    }

    /**
     */
    public ChannelOrderLogisticsGTO.UpdatePurchasePackageResult updatePurchasePackage(ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdatePurchasePackageMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ChannelOrderLogisticsFutureStub extends io.grpc.stub.AbstractStub<ChannelOrderLogisticsFutureStub> {
    private ChannelOrderLogisticsFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChannelOrderLogisticsFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected ChannelOrderLogisticsFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChannelOrderLogisticsFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult> getChannelOrderLogistics(
        ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetChannelOrderLogisticsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult> getChannerOrderIdByPackageId(
        ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetChannerOrderIdByPackageIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ChannelOrderLogisticsGTO.updateChannelOrderIdResult> updateChannelOrderId(
        ChannelOrderLogisticsGTO.updateChannelOrderIdRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateChannelOrderIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ChannelOrderLogisticsGTO.AddPurchaseOrderResult> addPurchaseOrder(
        ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAddPurchaseOrderMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ChannelOrderLogisticsGTO.UpdatePurchasePackageResult> updatePurchasePackage(
        ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdatePurchasePackageMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_CHANNEL_ORDER_LOGISTICS = 0;
  private static final int METHODID_GET_CHANNER_ORDER_ID_BY_PACKAGE_ID = 1;
  private static final int METHODID_UPDATE_CHANNEL_ORDER_ID = 2;
  private static final int METHODID_ADD_PURCHASE_ORDER = 3;
  private static final int METHODID_UPDATE_PURCHASE_PACKAGE = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ChannelOrderLogisticsImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ChannelOrderLogisticsImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_CHANNEL_ORDER_LOGISTICS:
          serviceImpl.getChannelOrderLogistics((ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest) request,
              (io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult>) responseObserver);
          break;
        case METHODID_GET_CHANNER_ORDER_ID_BY_PACKAGE_ID:
          serviceImpl.getChannerOrderIdByPackageId((ChannelOrderLogisticsGTO.ChannelOrderPackageIdRequest) request,
              (io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.ChannerOrderIdResponseResult>) responseObserver);
          break;
        case METHODID_UPDATE_CHANNEL_ORDER_ID:
          serviceImpl.updateChannelOrderId((ChannelOrderLogisticsGTO.updateChannelOrderIdRequest) request,
              (io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.updateChannelOrderIdResult>) responseObserver);
          break;
        case METHODID_ADD_PURCHASE_ORDER:
          serviceImpl.addPurchaseOrder((ChannelOrderLogisticsGTO.AddPurchaseOrderRequest) request,
              (io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.AddPurchaseOrderResult>) responseObserver);
          break;
        case METHODID_UPDATE_PURCHASE_PACKAGE:
          serviceImpl.updatePurchasePackage((ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest) request,
              (io.grpc.stub.StreamObserver<ChannelOrderLogisticsGTO.UpdatePurchasePackageResult>) responseObserver);
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

  private static abstract class ChannelOrderLogisticsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ChannelOrderLogisticsBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ChannelOrderLogisticsGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ChannelOrderLogistics");
    }
  }

  private static final class ChannelOrderLogisticsFileDescriptorSupplier
      extends ChannelOrderLogisticsBaseDescriptorSupplier {
    ChannelOrderLogisticsFileDescriptorSupplier() {}
  }

  private static final class ChannelOrderLogisticsMethodDescriptorSupplier
      extends ChannelOrderLogisticsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ChannelOrderLogisticsMethodDescriptorSupplier(String methodName) {
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
      synchronized (ChannelOrderLogisticsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ChannelOrderLogisticsFileDescriptorSupplier())
              .addMethod(getGetChannelOrderLogisticsMethod())
              .addMethod(getGetChannerOrderIdByPackageIdMethod())
              .addMethod(getUpdateChannelOrderIdMethod())
              .addMethod(getAddPurchaseOrderMethod())
              .addMethod(getUpdatePurchasePackageMethod())
              .build();
        }
      }
    }
    return result;
  }
}
