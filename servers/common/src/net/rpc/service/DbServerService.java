package net.rpc.service;

import net.proto.grpc.DbServiceGrpc;
import net.rpc.common.AbstractRpcService;

/**
 * @Author ckf
 */
public class DbServerService extends AbstractRpcService {

	/**
	 * 异步调用stub
	 */
	protected final DbServiceGrpc.DbServiceStub asyncStub;

	/**
	 * 同步调用stub
	 */
	protected final DbServiceGrpc.DbServiceBlockingStub blockingStub;

	/**
	 * futurestub
	 */
	protected final DbServiceGrpc.DbServiceFutureStub futureStub;

	public DbServerService(String ip, int port) {
		super(ip, port);
		asyncStub = DbServiceGrpc.newStub(channel);
		blockingStub = DbServiceGrpc.newBlockingStub(channel);
		futureStub = DbServiceGrpc.newFutureStub(channel);
	}

	public DbServiceGrpc.DbServiceStub getAsyncStub() {
		return asyncStub;
	}

	public DbServiceGrpc.DbServiceBlockingStub getBlockingStub() {
		return blockingStub;
	}

	public DbServiceGrpc.DbServiceFutureStub getFutureStub() {
		return futureStub;
	}
}
