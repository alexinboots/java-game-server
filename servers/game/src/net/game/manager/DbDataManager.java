package net.game.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.db.BasePo;
import net.game.manager.RpcManager;
import net.proto.grpc.DbRpcService;
import net.rpc.service.DbServerService;
import net.utils.JsonUtils;

/**
 * 数据库管理器
 *
 * @Author ckf
 */
@Service 
public class DbDataManager {

	private static Logger logger = LoggerFactory.getLogger(DbDataManager.class);

	@Autowired RpcManager rpcManager;

	@PostConstruct public void init() {

	}

	public <T extends BasePo> T select(int dsId, Class<T> clazz, long id) {
		DbServerService dbService = rpcManager.getDbServiceByDsId(dsId);
		if (dbService == null) {
			return null;
		}

		DbRpcService.SelectRequest request = DbRpcService.SelectRequest.newBuilder()
				.setClassName(clazz.getSimpleName()).setId(id).build();
		DbRpcService.SelectResponse select = dbService.getBlockingStub().select(request);
		String r = select.getR();
		return JsonUtils.string2Object(r, clazz);
	}
}
