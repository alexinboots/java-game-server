package net.rpc.common;

/**
 * 失效服务节点信息
 * 
 * @Author ckf
 */
public class InvalidServerInfo {

	/**
	 * 服务id
	 */
	private int serverId;

	/**
	 * 失效时间
	 */
	private long invalidTime;
	
	public static InvalidServerInfo valueOf(int serverId) {
		InvalidServerInfo i = new InvalidServerInfo();
		i.serverId = serverId;
		i.invalidTime = System.currentTimeMillis();
		return i;
	}

	public int getServerId() {
		return serverId;
	}

	public long getInvalidTime() {
		return invalidTime;
	}
}
