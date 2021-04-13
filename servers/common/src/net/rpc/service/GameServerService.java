package net.rpc.service;

import net.proto.grpc.GameServiceGrpc;
import net.rpc.common.AbstractRpcService;

/**
 * 游戏服grpc服务
 * 
 * @Author ckf
 */
public class GameServerService extends AbstractRpcService {

	/**
	 * 异步调用stub
	 */
	protected final GameServiceGrpc.GameServiceStub asyncStub;

	/**
	 * 同步调用stub
	 */
	protected final GameServiceGrpc.GameServiceBlockingStub blockingStub;

	/**
	 * futurestub
	 */
	protected final GameServiceGrpc.GameServiceFutureStub futureStub;

	public GameServerService(String ip, int port) {
		super(ip, port);
		asyncStub = GameServiceGrpc.newStub(channel);
		blockingStub = GameServiceGrpc.newBlockingStub(channel);
		futureStub = GameServiceGrpc.newFutureStub(channel);
	}

	public GameServiceGrpc.GameServiceBlockingStub getBlockingStub() {
		return blockingStub;
	}

	public GameServiceGrpc.GameServiceStub getAsyncStub() {
		return asyncStub;
	}

	public GameServiceGrpc.GameServiceFutureStub getFutureStub() {
		return futureStub;
	}
}
