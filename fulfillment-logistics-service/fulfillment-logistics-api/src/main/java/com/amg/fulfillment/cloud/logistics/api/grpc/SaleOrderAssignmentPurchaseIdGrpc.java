package com.amg.fulfillment.cloud.logistics.api.grpc;

import com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderAssignmentPurchaseIdGTO;

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
    comments = "Source: saleOrderAssignmentPurshaseId.proto")
public final class SaleOrderAssignmentPurchaseIdGrpc {

  private SaleOrderAssignmentPurchaseIdGrpc() {}

  public static final String SERVICE_NAME = "SaleOrderAssignmentPurchaseId";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest,
      SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> getAssignmentPurchaseIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "assignmentPurchaseId",
      requestType = SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest.class,
      responseType = SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest,
      SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> getAssignmentPurchaseIdMethod() {
    io.grpc.MethodDescriptor<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest, SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> getAssignmentPurchaseIdMethod;
    if ((getAssignmentPurchaseIdMethod = SaleOrderAssignmentPurchaseIdGrpc.getAssignmentPurchaseIdMethod) == null) {
      synchronized (SaleOrderAssignmentPurchaseIdGrpc.class) {
        if ((getAssignmentPurchaseIdMethod = SaleOrderAssignmentPurchaseIdGrpc.getAssignmentPurchaseIdMethod) == null) {
          SaleOrderAssignmentPurchaseIdGrpc.getAssignmentPurchaseIdMethod = getAssignmentPurchaseIdMethod = 
              io.grpc.MethodDescriptor.<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest, SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "SaleOrderAssignmentPurchaseId", "assignmentPurchaseId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult.getDefaultInstance()))
                  .setSchemaDescriptor(new SaleOrderAssignmentPurchaseIdMethodDescriptorSupplier("assignmentPurchaseId"))
                  .build();
          }
        }
     }
     return getAssignmentPurchaseIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SaleOrderAssignmentPurchaseIdStub newStub(io.grpc.Channel channel) {
    return new SaleOrderAssignmentPurchaseIdStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SaleOrderAssignmentPurchaseIdBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SaleOrderAssignmentPurchaseIdBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SaleOrderAssignmentPurchaseIdFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SaleOrderAssignmentPurchaseIdFutureStub(channel);
  }

  /**
   */
  public static abstract class SaleOrderAssignmentPurchaseIdImplBase implements io.grpc.BindableService {

    /**
     */
    public java.util.List<com.amg.fulfillment.cloud.logistics.api.proto.SaleOrderAssignmentPurchaseIdGTO.SaleOrderPurchaseIdResponse> assignmentPurchaseId(SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request,
                                     io.grpc.stub.StreamObserver<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> responseObserver) {
			return null;
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAssignmentPurchaseIdMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest,
                SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult>(
                  this, METHODID_ASSIGNMENT_PURCHASE_ID)))
          .build();
    }
  }

  /**
   */
  public static final class SaleOrderAssignmentPurchaseIdStub extends io.grpc.stub.AbstractStub<SaleOrderAssignmentPurchaseIdStub> {
    private SaleOrderAssignmentPurchaseIdStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderAssignmentPurchaseIdStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderAssignmentPurchaseIdStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderAssignmentPurchaseIdStub(channel, callOptions);
    }

    /**
     */
    public void assignmentPurchaseId(SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request,
                                     io.grpc.stub.StreamObserver<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAssignmentPurchaseIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SaleOrderAssignmentPurchaseIdBlockingStub extends io.grpc.stub.AbstractStub<SaleOrderAssignmentPurchaseIdBlockingStub> {
    private SaleOrderAssignmentPurchaseIdBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderAssignmentPurchaseIdBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderAssignmentPurchaseIdBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderAssignmentPurchaseIdBlockingStub(channel, callOptions);
    }

    /**
     */
    public SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult assignmentPurchaseId(SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request) {
      return blockingUnaryCall(
          getChannel(), getAssignmentPurchaseIdMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SaleOrderAssignmentPurchaseIdFutureStub extends io.grpc.stub.AbstractStub<SaleOrderAssignmentPurchaseIdFutureStub> {
    private SaleOrderAssignmentPurchaseIdFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SaleOrderAssignmentPurchaseIdFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SaleOrderAssignmentPurchaseIdFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SaleOrderAssignmentPurchaseIdFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult> assignmentPurchaseId(
        SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAssignmentPurchaseIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ASSIGNMENT_PURCHASE_ID = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SaleOrderAssignmentPurchaseIdImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SaleOrderAssignmentPurchaseIdImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ASSIGNMENT_PURCHASE_ID:
          serviceImpl.assignmentPurchaseId((SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest) request,
              (io.grpc.stub.StreamObserver<SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdResult>) responseObserver);
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

  private static abstract class SaleOrderAssignmentPurchaseIdBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SaleOrderAssignmentPurchaseIdBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SaleOrderAssignmentPurchaseIdGTO.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SaleOrderAssignmentPurchaseId");
    }
  }

  private static final class SaleOrderAssignmentPurchaseIdFileDescriptorSupplier
      extends SaleOrderAssignmentPurchaseIdBaseDescriptorSupplier {
    SaleOrderAssignmentPurchaseIdFileDescriptorSupplier() {}
  }

  private static final class SaleOrderAssignmentPurchaseIdMethodDescriptorSupplier
      extends SaleOrderAssignmentPurchaseIdBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SaleOrderAssignmentPurchaseIdMethodDescriptorSupplier(String methodName) {
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
      synchronized (SaleOrderAssignmentPurchaseIdGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SaleOrderAssignmentPurchaseIdFileDescriptorSupplier())
              .addMethod(getAssignmentPurchaseIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}
