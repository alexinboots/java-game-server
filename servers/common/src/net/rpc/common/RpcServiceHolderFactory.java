package net.rpc.common;

import net.define.common.ServerType;
import net.proto.grpc.DbRpcService;
import net.rpc.service.DbServerService;
import net.rpc.service.GameServerService;
import net.server.ServerInfo;

/**
 * rpc服务持有者工厂
 * 
 * @Author ckf
 */
public class RpcServiceHolderFactory {

	/**
	 * 创建服务持有者  此时可以根据服务器类型放入自定义的选择策略
	 * 
	 * @param serverType 
	 * @return
	 */
	public static AbstractRpcServiceHolder createHolderByServerType(ServerType serverType) {
		AbstractRpcServiceHolder holder = null;
		switch (serverType) {
			case MANAGER:
				holder = new AbstractRpcServiceHolder(serverType, 60000) {
					@Override public Object initService(ServerInfo serverInfo) {
						//TODO : 根据服务类型放入特定的service
						return null;
					}
				};
				break;
			case GAME:
				holder = new AbstractRpcServiceHolder(serverType, 60000) {
					@Override public Object initService(ServerInfo serverInfo) {
						return new GameServerService(serverInfo.getWanIp(), serverInfo.getRpcPort());
					}
				};
				break;
			case DB:
				holder = new AbstractRpcServiceHolder(serverType, 60000) {
					@Override public Object initService(ServerInfo serverInfo) {
						return new DbServerService(serverInfo.getWanIp(), serverInfo.getRpcPort());
					}
				};
			break;
			default:
				break;
		}
		
		return holder;
	}
}
