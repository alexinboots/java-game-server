package net.server;

import java.util.Set;

/**
 * 有数据保存需求的服务器
 * 
 * @Author ckf
 */
public class DataServerInfo extends ServerInfo {

	/**
	 * redis ip
	 */
	private String redisIp;

	/**
	 * redis port 
	 */
	private int redisPort;

	/**
	 * redis密码
	 */
	private String redisPass;

	/**
	 * 关联的数据服
	 */
	private Set<Integer> dbServerIds;
	
	public String getRedisIp() {
		return redisIp;
	}

	public void setRedisIp(String redisIp) {
		this.redisIp = redisIp;
	}

	public int getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(int redisPort) {
		this.redisPort = redisPort;
	}

	public String getRedisPass() {
		return redisPass;
	}

	public void setRedisPass(String redisPass) {
		this.redisPass = redisPass;
	}

	public Set<Integer> getDbServerIds() {
		return dbServerIds;
	}

	public void setDbServerIds(Set<Integer> dbServerIds) {
		this.dbServerIds = dbServerIds;
	}
}
