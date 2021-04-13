package net.rpc;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.AbstractEventListener;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.typesafe.config.ConfigException;
import io.grpc.Server;
import net.define.common.ServerType;
import net.rpc.common.AbstractRpcServiceHolder;
import net.rpc.common.RpcServiceHolderFactory;
import net.server.ServerInfo;
import net.utils.PropertiesUtils;
import net.utils.thread.Scheduler;

/**
 * nacos注册服务
 * 这里没有选择向上抽象出接口  因为不同的服务发现工具处理流程不同
 *
 * @Author ckf
 */
public abstract class AbstractNacosRegister {

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * nacos服务地址
	 */
	private String nacosAddr;

	/**
	 * 所属组
	 */
	private String group;

	/**
	 * nacos超时时间
	 */
	private int nacosTimeOut;

	/**
	 * 需要检测的未订阅服务间隔时间
	 */
	private int checkNoSubscribeServicesSec;
	
	/**
	 * nacos发现服务
	 */
	private NamingService nacosNameingService;

	/**
	 * event处理线程池
	 */
	private Executor executor;

	/**
	 * 需要订阅的服务
	 */
	protected Set<String> subscribeServices = new ConcurrentHashSet<>();

	/**
	 * 未订阅但是需要监控的服务 原因在handleevent方法注释
	 */
	protected Set<String> noSubscribeServices = new ConcurrentHashSet<>();
	
	/**
	 * 本机提供的rpc服务
	 */
	protected Server server;

	/**
	 * 其他服务器提供的rpc服务持有者
	 */
	protected Map<Integer, AbstractRpcServiceHolder> rpcServiceHolders = new ConcurrentHashMap<>();

	/**
	 * 根据配置初始化并注册
	 *
	 * @param serverInfo
	 * @param confPath
	 * @throws NacosException
	 */
	protected void init(ServerInfo serverInfo, String confPath) throws NacosException {
		File filePath = new File(confPath);
		if (filePath.exists() && filePath.isFile()) {
			logger.debug("use external nacos config file. file={}", filePath.getAbsoluteFile());

		} else {
			URL fileUrl = this.getClass().getClassLoader().getResource(confPath);

			//依然读取失败，则抛出异常
			if (fileUrl == null) {
				throw new ConfigException("读取nacos的配置文件失败，file=" + confPath) {
				};
			}

			filePath = new File(fileUrl.getPath());
		}

		Properties confProp = PropertiesUtils.load(filePath);
		nacosAddr = confProp.getProperty("serverAddr");
		group = confProp.getProperty("group");
		nacosTimeOut = Integer.parseInt(confProp.getProperty("timeout"));
		nacosNameingService = NamingFactory.createNamingService(nacosAddr);
		checkNoSubscribeServicesSec = Integer.parseInt(confProp.getProperty("nosubPeriod")) * 1000;
		if (checkNoSubscribeServicesSec < 10000) {
			checkNoSubscribeServicesSec = 10000;
		}
		
		//注册自己到nacos
		this.regist(serverInfo);
		//添加需要订阅的服务记录
		this.addSubscribeServices();
		//订阅
		this.subscribe();
		//处理非订阅但是需要维护的服务列表
		this.checkNoSubscibeService();
	}

	/**
	 * 终止服务
	 */
	public void stop() {
		logger.info("rpc服务停止");
		if (server != null) {
			try {
				server.awaitTermination();
			} catch (InterruptedException e) {
				logger.error("");
			}
		}
	}

	/**
	 * 注册本服到nacos
	 *
	 * @param serverInfo
	 * @throws NacosException
	 */
	private void regist(ServerInfo serverInfo) throws NacosException {
		Instance instance = new Instance();
		instance.setIp(serverInfo.getWanIp());
		instance.setPort(serverInfo.getRpcPort());
		instance.setHealthy(false);
		instance.setWeight(2.0);
		Map<String, String> instanceMeta = new HashMap<>();
		instanceMeta.put("serverType", String.valueOf(serverInfo.getServerType().getValue()));
		instanceMeta.put("serverId", String.valueOf(serverInfo.getServerId()));
		this.addInstanceMeta(instanceMeta);
		
		instance.setMetadata(instanceMeta);
		
		nacosNameingService.registerInstance(serverInfo.getServerType().getName(), group, instance);

		executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				new ThreadFactory() {
					@Override public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("nacos");
						return thread;
					}
				});
	}

	/**
	 * 向nacos订阅需要的服务
	 */
	protected void subscribe() {
		this.addSubscribeServices();
		if (CollectionUtils.isEmpty(this.subscribeServices)) {
			logger.error("订阅的服务列表为空");
			return;
		}

		for (String subscribeService : subscribeServices) {
			try {
				nacosNameingService.subscribe(subscribeService, group, new AbstractEventListener() {

					//EventListener onEvent is sync to handle, If process too low in onEvent, maybe block other onEvent callback.
					//So you can override getExecutor() to async handle event.
					@Override public Executor getExecutor() {
						return executor;
					}

					@Override public void onEvent(Event event) {
						handleEvent(event);
					}
				});
			} catch (NacosException e) {
				logger.error("订阅服务出现异常 服务名 = {} e = ", subscribeService, e);
			}
		}
	}

	/**
	 * 处理nacos传来的事件
	 * nacos在0.9版本之后就不再支持服务内单个节点的变化推送 订阅服务的节点发生变化事件中拿到的是
	 * 服务内的全部节点
	 * 所以处理游戏服战斗服数据服这种节点特别多的服务时不建议走nacos的订阅事件 可以依赖服务器定时从nacos拿到
	 * 服务内的所有节点来进行维护
	 *
	 * 待优化 TODO : 可以把0.9之前版本的节点状态变更增量推送代码改到当前使用的nacos版本中
	 * 但是根据自己项目需求改这类服务发现工具的源码都会遇到同一个问题，就是对于工具修复bug和拓展
	 * 功能时的更新不能直接使用，需要检查新版本源码中可能对自己修改过的内容起到的影响的部分
	 *
	 * @param event
	 */
	private void handleEvent(Event event) {
		NamingEvent namingEvent = (NamingEvent) event;
		//这里根据推服务状态变化事件中的节点是增量还是全量走不同方法处理,这里用全量
		totalInstanceHandle(namingEvent.getInstances());
	}

	/**
	 * 处理订阅服务中全量推送过来的节点信息
	 *
	 * @param instances 服务内的全部节点
	 */
	private synchronized void totalInstanceHandle(List<Instance> instances) {
		//有效的服务id集合,用来做存活检测
		Map<Integer, Set<Integer>> activeServers = new HashMap<>();
		if (!CollectionUtils.isEmpty(instances)) {
			for (Instance instance : instances) {
				if (!instance.isHealthy() || !instance.isEnabled()) {
					continue;
				}

				String ip = instance.getIp();
				int port = instance.getPort();
				//取得服务的信息
				String serverIdStr = instance.getMetadata().get("serverId");
				String serverTypeStr = instance.getMetadata().get("serverType");
				if (serverIdStr == null || serverTypeStr == null) {
					logger.error("nacosevent metadata错误 ip = {} port = {} serverId = {} serverType = {}", ip, port,
							serverIdStr, serverTypeStr);
					return;
				}

				int serverType = Integer.parseInt(serverTypeStr);
				int serverId = Integer.parseInt(serverIdStr);
				ServerInfo serverInfo = ServerInfo.valueOf(serverId, ServerType.valueOf(serverType), ip, port);

				handleNacosEvent(serverInfo, instance.getMetadata());

				this.addRpcServer(serverInfo);

				Set<Integer> serverIds = activeServers.get(serverType);
				if (serverIds == null) {
					serverIds = new HashSet<>();
					activeServers.put(serverType, serverIds);
				}

				serverIds.add(serverId);
			}
		}

		this.checkInvalidServer(activeServers);
	}

	/**
	 * 处理订阅服务中传输过来的变化节点信息
	 *
	 * @param instances 服务内的变化节点
	 */
	private synchronized void increInstanceHandle(List<Instance> instances) {
		if (CollectionUtils.isEmpty(instances)) {
			return;
		}
		
		for (Instance instance : instances) {
			if (!instance.isHealthy() || !instance.isEnabled()) {
				removeInstance(instance);
				continue;
			}

			String ip = instance.getIp();
			int port = instance.getPort();
			//取得服务的信息
			String serverIdStr = instance.getMetadata().get("serverId");
			String serverTypeStr = instance.getMetadata().get("serverType");
			if (serverIdStr == null || serverTypeStr == null) {
				logger.error("nacosevent metadata错误 ip = {} port = {} serverId = {} serverType = {}", ip, port,
						serverIdStr, serverTypeStr);
				return;
			}

			int serverType = Integer.parseInt(serverTypeStr);
			int serverId = Integer.parseInt(serverIdStr);
			ServerInfo serverInfo = ServerInfo.valueOf(serverId, ServerType.valueOf(serverType), ip, port);

			handleNacosEvent(serverInfo, instance.getMetadata());

			this.addRpcServer(serverInfo);
		}
	}

	/**
	 * 添加服务器信息到rpc服务持有者
	 *
	 * @param serverInfo
	 */
	public void addRpcServer(ServerInfo serverInfo) {
		AbstractRpcServiceHolder abstractRpcServiceHolder = rpcServiceHolders
				.get(serverInfo.getServerType().getValue());
		if (abstractRpcServiceHolder == null) {
			abstractRpcServiceHolder = RpcServiceHolderFactory.createHolderByServerType(serverInfo.getServerType());
			AbstractRpcServiceHolder exists = rpcServiceHolders
					.putIfAbsent(serverInfo.getServerType().getValue(), abstractRpcServiceHolder);
			if (exists != null) {
				abstractRpcServiceHolder = exists;
			}
		}

		abstractRpcServiceHolder.handleServerInfo(serverInfo);
	}

	/**
	 * 用来处理未订阅服务的节点更新 原因见上handleEvent方法注释
	 */
	protected void checkNoSubscibeService() {
		Scheduler.submit(new Runnable() {
			@Override public void run() {
				for (String serviceName : noSubscribeServices) {
					try {
						List<Instance> allInstances = nacosNameingService.getAllInstances(serviceName);
						totalInstanceHandle(allInstances);
					} catch (NacosException e) {
						logger.error("定时检测未订阅服务节点异常 serviceName = {} e = ", serviceName, e);
						continue;
					}
				}
				
				checkNoSubscibeService();
			}
		}, checkNoSubscribeServicesSec);
	}
	
	/**
	 * 移除服务中的节点，这里先把失效的服务放入一个失效缓存，在取得指定服务时判定是否
	 * 在失效缓存中，如果在失效缓存则证明服务不可用。
	 * 没有选择直接移除服务的原因是网络波动可能导致某些节点状态不够稳定，在短暂时间后可
	 * 能会恢复连接，此时直接从失效缓存移除即可。
	 * 失效服务清理依赖服务器的定时检测，如果失效时间和当前时间差大于阈值则将服务从缓存中全部移除
	 *
	 * @param instance
	 */
	protected void removeInstance(Instance instance) {
		if (instance == null) {
			return;
		}
		
		//取得服务的信息
		String serverIdStr = instance.getMetadata().get("serverId");
		String serverTypeStr = instance.getMetadata().get("serverType");
		if (serverIdStr == null || serverTypeStr == null) {
			logger.error("移除instance nacosevent metadata错误 ip = {} port = {} serverId = {} serverType = {}",
					instance.getIp(), instance.getPort(), serverIdStr, serverTypeStr);
			return;
		}

		int serverType = Integer.parseInt(serverTypeStr);
		int serverId = Integer.parseInt(serverIdStr);
		this.removeServerInfo(serverType, serverId);
	}

	/**
	 * 移除缓存的服务
	 *
	 * @param serverType
	 * @param serverId
	 */
	public void removeServerInfo(int serverType, int serverId) {
		AbstractRpcServiceHolder holder = this.rpcServiceHolders.get(serverType);
		if (holder == null) {
			return;
		}

		holder.invalidServer(serverId);
	}

	/**
	 * 检测失效服务
	 */
	private void checkInvalidServer(Map<Integer, Set<Integer>> activeServers) {
		for (Map.Entry<Integer, Set<Integer>> entry : activeServers.entrySet()) {
			Integer serverType = entry.getKey();
			Set<Integer> activeServerIds = entry.getValue();
			AbstractRpcServiceHolder holder = this.rpcServiceHolders.get(serverType);
			if (holder == null) {
				continue;
			}

			holder.checkInvalidServer(activeServerIds);
		}
	}

	/**
	 * 添加需要检测节点但是不走订阅的服务信息
	 * 
	 * @param serviceName 
	 */
	protected void addNoSubscribeService(String serviceName) {
		this.noSubscribeServices.add(serviceName);
	}

	/**
	 * 各服务器特殊处理发现的服务
	 *
	 * @param serverInfo
	 */
	abstract protected void handleNacosEvent(ServerInfo serverInfo, Map<String, String> metaData);

	/**
	 * 添加需要订阅的服务
	 */
	abstract protected void addSubscribeServices();

	/**
	 * 添加本服注册过去的元数据
	 */
	protected void addInstanceMeta(Map<String, String> instanceMeta) {
		//todo ：每个服务根据自己需求重写
	}

}