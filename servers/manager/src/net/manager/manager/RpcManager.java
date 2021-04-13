package net.manager.manager;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.nacos.api.exception.NacosException;
import net.define.common.ServerType;
import net.manager.ManagerMain;
import net.rpc.AbstractNacosRegister;
import net.rpc.common.AbstractRpcServiceHolder;
import net.rpc.service.DbServerService;
import net.rpc.service.GameServerService;
import net.server.ServerInfo;
import net.utils.AppUtils;

/**
 * rpc服务管理器
 *
 * @Author ckf
 */
@Service public class RpcManager extends AbstractNacosRegister {

	private static Logger logger = LoggerFactory.getLogger(RpcManager.class);

	@PostConstruct public void init() {
		this.initRegist();
		this.initRpcServer();
	}

	/**
	 * 向服务中心注册自己
	 */
	private void initRegist() {
		String filePath = AppUtils.getConfigFile("nacos.properties");
		try {
			super.init(ManagerMain.serverInfo, filePath);
		} catch (NacosException e) {
			logger.error("服务器rpc服务启动失败 e = ", e);
		}
	}

	/**
	 * 初始化本服提供的rpc服务
	 */
	private void initRpcServer() {

	}

	/**
	 * 服务器关闭时的操作
	 */
	@PreDestroy public void onServerStop() {
		stop();
	}

	/**
	 * 根据服务器id取得游戏服服务
	 *
	 * @param serverId
	 * @return
	 */
	public GameServerService getGameServerService(int serverId) {
		AbstractRpcServiceHolder<GameServerService> serviceHolder = rpcServiceHolders.get(ServerType.GAME.getValue());
		if (serviceHolder == null) {
			return null;
		}

		return serviceHolder.getService(serverId);
	}

	/**
	 * 根据服务器id取得游戏服服务
	 *
	 * @param serverId
	 * @return
	 */
	public DbServerService getDbServerService(int serverId) {
		AbstractRpcServiceHolder<DbServerService> serviceHolder = rpcServiceHolders.get(ServerType.DB.getValue());
		if (serviceHolder == null) {
			return null;
		}

		return serviceHolder.getService(serverId);
	}
	
	@Override protected void addSubscribeServices() {
		subscribeServices.add(ServerType.GAME.getName());
		subscribeServices.add(ServerType.DB.getName());
	}

	@Override protected void handleNacosEvent(ServerInfo serverInfo, Map<String, String> metaData) {
		
	}
}
