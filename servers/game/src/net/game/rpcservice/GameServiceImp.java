package net.game.rpcservice;

import io.grpc.stub.StreamObserver;
import net.proto.grpc.GameRpcService;
import net.proto.grpc.GameServiceGrpc;

/**
 * 游戏服rpc实现
 * 
 * @author ckf
 */
public class GameServiceImp extends GameServiceGrpc.GameServiceImplBase {

		@Override public void gameTest(GameRpcService.TestRequest req,
				StreamObserver<GameRpcService.TestResponse> responseObserver) {
			GameRpcService.TestResponse reply = GameRpcService.TestResponse.newBuilder().setParam1("test").build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
			
		}
	}
