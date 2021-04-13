package net.db.rpcservice;

import org.springframework.beans.factory.annotation.Autowired;
import io.grpc.stub.StreamObserver;
import net.db.DbMain;
import net.db.manager.DataManager;
import net.db.manager.RpcManager;
import net.proto.grpc.DbRpcService;
import net.proto.grpc.DbServiceGrpc;

/**
 * 数据服务
 * 
 * @Author ckf
 */
public class DbServiceImpl extends DbServiceGrpc.DbServiceImplBase {
	
	@Override public void select(DbRpcService.SelectRequest req,
			StreamObserver<DbRpcService.SelectResponse> responseObserver) {
		RpcManager rpcManager = (RpcManager) DbMain.context.getBean("rpcManager");
		DataManager dataManager = (DataManager) DbMain.context.getBean("dataManager");
		String s = dataManager.getData().get(req.getDsId(), req.getClassName(), req.getId());
		DbRpcService.SelectResponse reply = DbRpcService.SelectResponse.newBuilder().setR(s).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();

	}
}

