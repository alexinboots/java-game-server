package net.db.manager;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.nacos.api.exception.NacosException;
import io.grpc.ServerBuilder;
import net.db.DbMain;
import net.db.rpcservice.DbServiceImpl;
import net.rpc.AbstractNacosRegister;
import net.server.ServerInfo;
import net.utils.AppUtils;
import net.utils.JsonUtils;

/**
 * rpc服务
 *
 * @Author ckf
 */
@Service public class RpcManager extends AbstractNacosRegister {

	private static Logger logger = LoggerFactory.getLogger(RpcManager.class);

	@Autowired DataManager dataManager;

	@PostConstruct public void init() {
		this.registToNacos();
		this.initRpcServer();
	}

	private void registToNacos() {
		String filePath = AppUtils.getConfigFile("nacos.properties");
		try {
			super.init(DbMain.serverInfo, filePath);
		} catch (NacosException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化本服提供的rpc服务
	 */
	protected void initRpcServer() {
		try {
			server = ServerBuilder.forPort(DbMain.serverInfo.getRpcPort()).addService(new DbServiceImpl()).build()
					.start();
		} catch (IOException e) {
			logger.error("rpc服务启动失败 e = ", e);
			return;
		}
	}

	public void onServerStop() {
		stop();
	}

	@Override protected void addSubscribeServices() {
		//TODO ：根据需求放入需要订阅的服务
	}

	@Override protected void addInstanceMeta(Map<String, String> instanceMeta) {
		instanceMeta.put("dsIds", JsonUtils.object2String(dataManager.getDsIds()));
	}

	@Override protected void handleNacosEvent(ServerInfo serverInfo, Map<String, String> metaData) {
		
	}
}
