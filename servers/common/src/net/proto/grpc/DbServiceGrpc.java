package net.proto.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.36.0)",
    comments = "Source: pb/dbservice.proto")
public final class DbServiceGrpc {

  private DbServiceGrpc() {}

  public static final String SERVICE_NAME = "DbService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<DbRpcService.SelectRequest,
      DbRpcService.SelectResponse> getSelectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "select",
      requestType = DbRpcService.SelectRequest.class,
      responseType = DbRpcService.SelectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<DbRpcService.SelectRequest,
      DbRpcService.SelectResponse> getSelectMethod() {
    io.grpc.MethodDescriptor<DbRpcService.SelectRequest, DbRpcService.SelectResponse> getSelectMethod;
    if ((getSelectMethod = DbServiceGrpc.getSelectMethod) == null) {
      synchronized (DbServiceGrpc.class) {
        if ((getSelectMethod = DbServiceGrpc.getSelectMethod) == null) {
          DbServiceGrpc.getSelectMethod = getSelectMethod =
              io.grpc.MethodDescriptor.<DbRpcService.SelectRequest, DbRpcService.SelectResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "select"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DbRpcService.SelectRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DbRpcService.SelectResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DbServiceMethodDescriptorSupplier("select"))
              .build();
        }
      }
    }
    return getSelectMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DbServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DbServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DbServiceStub>() {
        @Override
        public DbServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DbServiceStub(channel, callOptions);
        }
      };
    return DbServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DbServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DbServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DbServiceBlockingStub>() {
        @Override
        public DbServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DbServiceBlockingStub(channel, callOptions);
        }
      };
    return DbServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DbServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DbServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DbServiceFutureStub>() {
        @Override
        public DbServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DbServiceFutureStub(channel, callOptions);
        }
      };
    return DbServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class DbServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void select(DbRpcService.SelectRequest request,
        io.grpc.stub.StreamObserver<DbRpcService.SelectResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSelectMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSelectMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                DbRpcService.SelectRequest,
                DbRpcService.SelectResponse>(
                  this, METHODID_SELECT)))
          .build();
    }
  }

  /**
   */
  public static final class DbServiceStub extends io.grpc.stub.AbstractAsyncStub<DbServiceStub> {
    private DbServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DbServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DbServiceStub(channel, callOptions);
    }

    /**
     */
    public void select(DbRpcService.SelectRequest request,
        io.grpc.stub.StreamObserver<DbRpcService.SelectResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSelectMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DbServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<DbServiceBlockingStub> {
    private DbServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DbServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DbServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public DbRpcService.SelectResponse select(DbRpcService.SelectRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSelectMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DbServiceFutureStub extends io.grpc.stub.AbstractFutureStub<DbServiceFutureStub> {
    private DbServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected DbServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DbServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<DbRpcService.SelectResponse> select(
        DbRpcService.SelectRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSelectMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SELECT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DbServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DbServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SELECT:
          serviceImpl.select((DbRpcService.SelectRequest) request,
              (io.grpc.stub.StreamObserver<DbRpcService.SelectResponse>) responseObserver);
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

  private static abstract class DbServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DbServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return DbRpcService.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DbService");
    }
  }

  private static final class DbServiceFileDescriptorSupplier
      extends DbServiceBaseDescriptorSupplier {
    DbServiceFileDescriptorSupplier() {}
  }

  private static final class DbServiceMethodDescriptorSupplier
      extends DbServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DbServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DbServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DbServiceFileDescriptorSupplier())
              .addMethod(getSelectMethod())
              .build();
        }
      }
    }
    return result;
  }
}
