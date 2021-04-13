package net.server;

/**
 * @Author ckf
 */
public class RedisInfo {


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
}
