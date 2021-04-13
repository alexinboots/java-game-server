package net.rpc.common;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.CollectionUtils;
import net.define.common.ServerType;
import net.server.ServerInfo;

/**
 * rpc服务器持有者，用来管理服务中的节点
 * 
 * @Author ckf
 */
public abstract class AbstractRpcServiceHolder<T> {

	/**
	 * 持有的服务器类型
	 */
	private ServerType serverType;
	
	/**
	 * 服务器信息
	 */
	private Map<Integer, ServerInfo> serverInfos = new ConcurrentHashMap<>();

	/**
	 * 暂时下线的服务节点信息,如果一定周期后还未被恢复则被移除
	 */
	private Map<Integer, InvalidServerInfo> invalidServerInfos = new ConcurrentHashMap<>();
	
	/**
	 * 具体的rpc服务 采用懒加载
	 */
	private Map<Integer, T> serverRpcServices = new ConcurrentHashMap<>();

	/**
	 * 服务器选择策略 用于负载均衡等
	 */
	private SelectStrategy selectStrategy;

	/**
	 * 失效服务缓存保存时间毫秒
	 */
	private int invalidClearTime; 
	
	public AbstractRpcServiceHolder(ServerType serverType, int invalidClearTime) {
		this.serverType = serverType;
		this.invalidClearTime = invalidClearTime;
	}
	
	/**
	 * 处理服务发现通过过来的服务器信息
	 * 
	 * @param serverInfo 
	 */
	public void handleServerInfo(ServerInfo serverInfo) {
		this.renewServer(serverInfo.getServerId());
		this.serverInfos.putIfAbsent(serverInfo.getServerId(), serverInfo);
	}
	
	/**
	 * 根据服务器取得特定服的rpc服务
	 * 懒加载 同一个service内的服务器数量远多于需要取得rpc服务的数量
	 *
	 * @param serverId
	 * @return
	 */
	public T getService(int serverId) {
		InvalidServerInfo invalidServerInfo = this.invalidServerInfos.get(serverId);
		if (invalidServerInfo != null) {
			return null;
		}
		
		T t = this.serverRpcServices.get(serverId);
		if (t == null) {
			ServerInfo serverInfo = this.serverInfos.get(serverId);
			if (serverInfo == null) {
				return null;
			}

			t = this.initService(serverInfo);
			if (t == null) {
				return null;
			}

			T exists = this.serverRpcServices.putIfAbsent(serverId, t);
			if (exists != null) {
				t = exists;
			}
		}

		return t;
	}

	/**
	 * 服务生效
	 * 
	 * @param serverId 
	 */
	public void renewServer(int serverId) {
		this.invalidServerInfos.remove(serverId);
	}

	/**
	 * 检测失效的服务节点
	 * 
	 * @param activeServerIds 正在生效的服务节点
	 */
	public void checkInvalidServer(Set<Integer> activeServerIds) {
		if (CollectionUtils.isEmpty(activeServerIds)) {
			return;
		}

		for (Integer serverId : this.serverInfos.keySet()) {
			if (activeServerIds.contains(serverId)) {
				continue;
			}
			
			invalidServer(serverId);
		}
	}
	
	/**
	 * 服务失效
	 * 
	 * @param serverId 
	 */
	public void invalidServer(int serverId) {
		ServerInfo serverInfo = this.serverInfos.get(serverId);
		if (serverInfo == null) {
			return;
		}
		
		this.invalidServerInfos.putIfAbsent(serverId, InvalidServerInfo.valueOf(serverId));
	}

	/**
	 * 检查并且清除过期的失效服务节点
	 */
	public void checkAndClearInvaildServer() {
		long now = System.currentTimeMillis();
		for (InvalidServerInfo invalidServerInfo : this.invalidServerInfos.values()) {
			if (now - invalidServerInfo.getInvalidTime() < invalidClearTime) {
				continue;
			}
			
			this.removeServer(invalidServerInfo.getServerId());
		}
	}
	
	/**
	 * 清除服务缓存
	 * 
	 * @param serverId 
	 */
	public void removeServer(int serverId) {
		this.invalidServerInfos.remove(serverId);
		this.serverInfos.remove(serverId);
		this.serverRpcServices.remove(serverId);
	}
	
	/**
	 * 取得默认的服务 用于group中只有一个服务的情况
	 * 
	 * @return 
	 */
	public T getDefaultService() {
		T t = null;
		for (T value : serverRpcServices.values()) {
			t = value;
			break;
		}
		
		if (t == null) {
			ServerInfo serverInfo = null;
			for (ServerInfo value : this.serverInfos.values()) {
				serverInfo = value;
				break;
			}
			
			if (serverInfo == null) {
				return null;
			}

			t = this.initService(serverInfo);
			if (t == null) {
				return null;
			}

			T exists = this.serverRpcServices.putIfAbsent(serverInfo.getServerId(), t);
			if (exists != null) {
				t = exists;
			}
		}

		return t;
	}

	/**
	 * 设置服务器选择策略
	 * 
	 * @param selectStrategy 
	 */
	public void setSelectStrategy(SelectStrategy selectStrategy) {
		this.selectStrategy = selectStrategy;
	}

	/**
	 * 用特定策略取得服务器
	 * 
	 * @return 
	 */
	public T getServiceWithStrategy() {
		int serverId = this.selectStrategy.selectServerId();
		return this.getService(serverId);
	}

	/**
	 * 设置过期服务节点清除时间
	 *
	 * @param invalidClearTime
	 */
	public void setInvalidClearTime(int invalidClearTime) {
		this.invalidClearTime = invalidClearTime;
	}
	
	/**
	 * 根据服务器信息初始化服务
	 * 不同的服务持有者自行实现自己的rpc调用类
	 * 
	 * @param serverInfo 
	 * @return
	 */
	abstract public T initService(ServerInfo serverInfo);
}
