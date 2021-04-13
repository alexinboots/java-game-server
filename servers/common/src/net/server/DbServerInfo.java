package net.server;

import java.util.Map;
import java.util.Set;

/**
 * 数据服信息
 * 
 * @Author ckf
 */
public class DbServerInfo extends DataServerInfo {

	/**
	 * 本服连接的数据库信息
	 */
	private Set<DbInfo> dbInfos;
	
	/**
	 * 数据服id对应的redis数据库信息 方便复用
	 */
	private Map<Integer, RedisInfo> redisInfos;
	
	/**
	 * redis连接超时时间
	 */
	private int redisExpireTime;

	public Set<DbInfo> getServerDbInfos() {
		return dbInfos;
	}

	public void setServerDbInfos(Set<DbInfo> dbInfos) {
		this.dbInfos = dbInfos;
	}

	public Map<Integer, RedisInfo> getRedisInfos() {
		return redisInfos;
	}

	public void setRedisInfos(Map<Integer, RedisInfo> redisInfos) {
		this.redisInfos = redisInfos;
	}

	public int getRedisExpireTime() {
		return redisExpireTime;
	}

	public void setRedisExpireTime(int redisExpireTime) {
		this.redisExpireTime = redisExpireTime;
	}
}
