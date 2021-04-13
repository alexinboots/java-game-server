package net.game.manager;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.grpc.ServerBuilder;
import net.define.common.ServerType;
import net.game.GameMain;
import net.game.rpcservice.GameServiceImp;
import net.rpc.AbstractNacosRegister;
import net.rpc.common.AbstractRpcServiceHolder;
import net.rpc.service.DbServerService;
import net.rpc.service.GameServerService;
import net.server.ServerInfo;
import net.utils.AppUtils;
import net.utils.JsonUtils;
import net.utils.StringUtils;

/**
 * rpc管理器
 * 
 * @Author ckf
 */
@Service public class RpcManager extends AbstractNacosRegister {

	private static Logger logger = LoggerFactory.getLogger(RpcManager.class);
	
	/**
	 * 数据中心id对应到的数据服id
	 */
	private Map<Integer, Integer> dsId2DbServerId = new ConcurrentHashMap<>();

	@PostConstruct public void init() {
		this.registToNacos();
		this.initRpcServer();
	}

	private void registToNacos() {
		String filePath = AppUtils.getConfigFile("nacos.properties");
		try {
			super.init(GameMain.serverInfo, filePath);
		} catch (NacosException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化本服提供的rpc服务
	 */
	protected void initRpcServer() {
		try {
			server = ServerBuilder.forPort(GameMain.serverInfo.getRpcPort()).addService(new GameServiceImp()).build()
					.start();
		} catch (IOException e) {
			logger.error("rpc服务启动失败 e = ", e);
			return;
		}
	}

	@PreDestroy
	public void onServerStop() {
		stop();
	}

	@Override protected void addSubscribeServices() {
		//TODO ：根据需求放入需要订阅的服务
	}

	@Override protected void handleNacosEvent(ServerInfo serverInfo, Map<String, String> metaData) {
		if (serverInfo.getServerType() == ServerType.DB) {
			String dsIdStr = metaData.get("dsIds");
			if (StringUtils.isNotBlank(dsIdStr)) {
				TypeReference<Set<Integer>> type = new TypeReference<Set<Integer>>() {
				};
				Set<Integer> dsIds = JsonUtils.string2Object(dsIdStr, type);
				if (CollectionUtils.isNotEmpty(dsIds)) {
					for (Integer dsId : dsIds) {
						this.dsId2DbServerId.put(dsId, serverInfo.getServerId());
					}
				}
			}
		}
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

	/**
	 * 根据服务器id取得游戏服服务
	 *
	 * @param dsId
	 * @return
	 */
	public DbServerService getDbServiceByDsId(int dsId) {
		Integer serverId = this.dsId2DbServerId.get(dsId);
		if (serverId == null) {
			logger.error("找不到dsId = {}对应的serverId", dsId);
			return null;
		}
		
		AbstractRpcServiceHolder<DbServerService> serviceHolder = rpcServiceHolders.get(ServerType.DB.getValue());
		if (serviceHolder == null) {
			return null;
		}

		DbServerService service = serviceHolder.getService(serverId);
		if (service == null) {
			logger.error("找不到dbserverId = {}对应的数据服", serverId);
		}
		
		return service;
	}
}
