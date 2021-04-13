package net.server;

import net.define.common.ServerType;

/**
 * rpc服务器信息
 * 
 * @Author ckf
 */
public class ServerInfo {

	/**
	 * 服务器id
	 */
	private int serverId;

	/**
	 * 服务器类型
	 */
	private ServerType serverType;

	/**
	 * 开放给外网的地址
	 */
	private String wanIp;

	/**
	 * rpc服务端口
	 */
	private int rpcPort;
	
	public ServerInfo() {
	}

	public static ServerInfo valueOf(int serverId, ServerType serverType, String wanIp, int rpcPort) {
		ServerInfo s = new ServerInfo();
		s.serverId = serverId;
		s.serverType = serverType;
		s.wanIp = wanIp;
		s.rpcPort = rpcPort;
		return s;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getWanIp() {
		return wanIp;
	}

	public void setWanIp(String wanIp) {
		this.wanIp = wanIp;
	}

	public int getRpcPort() {
		return rpcPort;
	}

	public void setRpcPort(int rpcPort) {
		this.rpcPort = rpcPort;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}
}
