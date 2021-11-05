package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.InventoryStoreGTO;

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
    comments = "Source: inventory-store.proto")
public final class InventoryStoreSrvGrpc {

  private InventoryStoreSrvGrpc() {}

  public static final String SERVICE_NAME = "inventorypb.InventoryStoreSrv";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<InventoryStoreGTO.GetSPUSizeTableReq,
      InventoryStoreGTO.GetSPUSizeTableReply> getGetSPUSizeTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetSPUSizeTable",
      requestType = InventoryStoreGTO.GetSPUSizeTableReq.class,
      responseType = InventoryStoreGTO.GetSPUSizeTableReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<InventoryStoreGTO.GetSPUSizeTableReq,
      InventoryStoreGTO.GetSPUSizeTableReply> getGetSPUSizeTableMethod() {
    io.grpc.MethodDescriptor<InventoryStoreGTO.GetSPUSizeTableReq, InventoryStoreGTO.GetSPUSizeTableReply> getGetSPUSizeTableMethod;
    if ((getGetSPUSizeTableMethod = InventoryStoreSrvGrpc.getGetSPUSizeTableMethod) == null) {
      synchronized (InventoryStoreSrvGrpc.class) {
        if ((getGetSPUSizeTableMethod = InventoryStoreSrvGrpc.getGetSPUSizeTableMethod) == null) {
          InventoryStoreSrvGrpc.getGetSPUSizeTableMethod = getGetSPUSizeTableMethod = 
              io.grpc.MethodDescriptor.<InventoryStoreGTO.GetSPUSizeTableReq, InventoryStoreGTO.GetSPUSizeTableReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "inventorypb.InventoryStoreSrv", "GetSPUSizeTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InventoryStoreGTO.GetSPUSizeTableReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InventoryStoreGTO.GetSPUSizeTableReply.getDefaultInstance()))
                  .setSchemaDescriptor(new InventoryStoreSrvMethodDescriptorSupplier("GetSPUSizeTable"))
                  .build();
          }
        }
     }
     return getGetSPUSizeTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<InventoryStoreGTO.GetCateIDSizeTableReq,
      InventoryStoreGTO.GetCateIDSizeTableReply> getGetCateIDSizeTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCateIDSizeTable",
      requestType = InventoryStoreGTO.GetCateIDSizeTableReq.class,
      responseType = InventoryStoreGTO.GetCateIDSizeTableReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<InventoryStoreGTO.GetCateIDSizeTableReq,
      InventoryStoreGTO.GetCateIDSizeTableReply> getGetCateIDSizeTableMethod() {
    io.grpc.MethodDescriptor<InventoryStoreGTO.GetCateIDSizeTableReq, InventoryStoreGTO.GetCateIDSizeTableReply> getGetCateIDSizeTableMethod;
    if ((getGetCateIDSizeTableMethod = InventoryStoreSrvGrpc.getGetCateIDSizeTableMethod) == null) {
      synchronized (InventoryStoreSrvGrpc.class) {
        if ((getGetCateIDSizeTableMethod = InventoryStoreSrvGrpc.getGetCateIDSizeTableMethod) == null) {
          InventoryStoreSrvGrpc.getGetCateIDSizeTableMethod = getGetCateIDSizeTableMethod = 
              io.grpc.MethodDescriptor.<InventoryStoreGTO.GetCateIDSizeTableReq, InventoryStoreGTO.GetCateIDSizeTableReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "inventorypb.InventoryStoreSrv", "GetCateIDSizeTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InventoryStoreGTO.GetCateIDSizeTableReq.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  InventoryStoreGTO.GetCateIDSizeTableReply.getDefaultInstance()))
                  .setSchemaDescriptor(new InventoryStoreSrvMethodDescriptorSupplier("GetCateIDSizeTable"))
                  .build();
          }
        }
     }
     return getGetCateIDSizeTableMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static InventoryStoreSrvStub newStub(io.grpc.Channel channel) {
    return new InventoryStoreSrvStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static InventoryStoreSrvBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new InventoryStoreSrvBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static InventoryStoreSrvFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new InventoryStoreSrvFutureStub(channel);
  }

  /**
   */
  public static abstract class InventoryStoreSrvImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * GetSPUSizeTable 获取某个 spu 的尺码表，包括操作者
     * </pre>
     */
    public void getSPUSizeTable(InventoryStoreGTO.GetSPUSizeTableReq request,
                                io.grpc.stub.StreamObserver<InventoryStoreGTO.GetSPUSizeTableReply> responseObserver) {
      asyncUnimplementedUnaryCall(getGetSPUSizeTableMethod(), responseObserver);
    }

    /**
     * <pre>
     * GetCateIDSizeTable 获取某个类目的尺码表，包括类目信息
     * </pre>
     */
    public void getCateIDSizeTable(InventoryStoreGTO.GetCateIDSizeTableReq request,
                                   io.grpc.stub.StreamObserver<InventoryStoreGTO.GetCateIDSizeTableReply> responseObserver) {
      asyncUnimplementedUnaryCall(getGetCateIDSizeTableMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetSPUSizeTableMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                InventoryStoreGTO.GetSPUSizeTableReq,
                InventoryStoreGTO.GetSPUSizeTableReply>(
                  this, METHODID_GET_SPUSIZE_TABLE)))
          .addMethod(
            getGetCateIDSizeTableMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                InventoryStoreGTO.GetCateIDSizeTableReq,
                InventoryStoreGTO.GetCateIDSizeTableReply>(
                  this, METHODID_GET_CATE_IDSIZE_TABLE)))
          .build();
    }
  }

  /**
   */
  public static final class InventoryStoreSrvStub extends io.grpc.stub.AbstractStub<InventoryStoreSrvStub> {
    private InventoryStoreSrvStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InventoryStoreSrvStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected InventoryStoreSrvStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InventoryStoreSrvStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetSPUSizeTable 获取某个 spu 的尺码表，包括操作者
     * </pre>
     */
    public void getSPUSizeTable(InventoryStoreGTO.GetSPUSizeTableReq request,
                                io.grpc.stub.StreamObserver<InventoryStoreGTO.GetSPUSizeTableReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetSPUSizeTableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * GetCateIDSizeTable 获取某个类目的尺码表，包括类目信息
     * </pre>
     */
    public void getCateIDSizeTable(InventoryStoreGTO.GetCateIDSizeTableReq request,
                                   io.grpc.stub.StreamObserver<InventoryStoreGTO.GetCateIDSizeTableReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetCateIDSizeTableMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class InventoryStoreSrvBlockingStub extends io.grpc.stub.AbstractStub<InventoryStoreSrvBlockingStub> {
    private InventoryStoreSrvBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InventoryStoreSrvBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected InventoryStoreSrvBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InventoryStoreSrvBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetSPUSizeTable 获取某个 spu 的尺码表，包括操作者
     * </pre>
     */
    public InventoryStoreGTO.GetSPUSizeTableReply getSPUSizeTable(InventoryStoreGTO.GetSPUSizeTableReq request) {
      return blockingUnaryCall(
          getChannel(), getGetSPUSizeTableMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * GetCateIDSizeTable 获取某个类目的尺码表，包括类目信息
     * </pre>
     */
    public InventoryStoreGTO.GetCateIDSizeTableReply getCateIDSizeTable(InventoryStoreGTO.GetCateIDSizeTableReq request) {
      return blockingUnaryCall(
          getChannel(), getGetCateIDSizeTableMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class InventoryStoreSrvFutureStub extends io.grpc.stub.AbstractStub<InventoryStoreSrvFutureStub> {
    private InventoryStoreSrvFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private InventoryStoreSrvFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected InventoryStoreSrvFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new InventoryStoreSrvFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * GetSPUSizeTable 获取某个 spu 的尺码表，包括操作者
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<InventoryStoreGTO.GetSPUSizeTableReply> getSPUSizeTable(
        InventoryStoreGTO.GetSPUSizeTableReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetSPUSizeTableMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * GetCateIDSizeTable 获取某个类目的尺码表，包括类目信息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<InventoryStoreGTO.GetCateIDSizeTableReply> getCateIDSizeTable(
        InventoryStoreGTO.GetCateIDSizeTableReq request) {
      return futureUnaryCall(
          getChannel().newCall(getGetCateIDSizeTableMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_SPUSIZE_TABLE = 0;
  private static final int METHODID_GET_CATE_IDSIZE_TABLE = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final InventoryStoreSrvImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(InventoryStoreSrvImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_SPUSIZE_TABLE:
          serviceImpl.getSPUSizeTable((InventoryStoreGTO.GetSPUSizeTableReq) request,
              (io.grpc.stub.StreamObserver<InventoryStoreGTO.GetSPUSizeTableReply>) responseObserver);
          break;
        case METHODID_GET_CATE_IDSIZE_TABLE:
          serviceImpl.getCateIDSizeTable((InventoryStoreGTO.GetCateIDSizeTableReq) request,
              (io.grpc.stub.StreamObserver<InventoryStoreGTO.GetCateIDSizeTableReply>) responseObserver);
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

  private static abstract class InventoryStoreSrvBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    InventoryStoreSrvBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return InventoryStoreGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("InventoryStoreSrv");
    }
  }

  private static final class InventoryStoreSrvFileDescriptorSupplier
      extends InventoryStoreSrvBaseDescriptorSupplier {
    InventoryStoreSrvFileDescriptorSupplier() {}
  }

  private static final class InventoryStoreSrvMethodDescriptorSupplier
      extends InventoryStoreSrvBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    InventoryStoreSrvMethodDescriptorSupplier(String methodName) {
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
      synchronized (InventoryStoreSrvGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new InventoryStoreSrvFileDescriptorSupplier())
              .addMethod(getGetSPUSizeTableMethod())
              .addMethod(getGetCateIDSizeTableMethod())
              .build();
        }
      }
    }
    return result;
  }
}
