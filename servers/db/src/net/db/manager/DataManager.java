package net.db.manager;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;
import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import net.db.DbMain;
import net.db.dao.AsyncData;
import net.db.dao.RedisCache;
import net.server.DbInfo;
import net.server.DbServerInfo;
import net.server.RedisInfo;
import net.utils.db.AutoDao;

/**
 * redis管理器
 * 
 * @Author ckf
 */
@Service
public class DataManager {
	
	private Map<Integer, RedisCache> redisCaches = new ConcurrentHashMap<>();
	
	private AsyncData data;

	/**
	 * 本服持有的数据库id
	 */
	private Set<Integer> dsIds = new ConcurrentHashSet<>();
	
	@PostConstruct
	public void init() throws Exception {
		initRedis();
		initDataSource();
		initData();
	}

	/**
	 * 根据配置初始化redis并且映射和数据库id映射
	 */
	private void initRedis() {
		DbServerInfo serverInfo = DbMain.serverInfo;
		for (Map.Entry<Integer, RedisInfo> entry : serverInfo.getRedisInfos().entrySet()) {
			Integer dbId = entry.getKey();
			RedisInfo redisInfo = entry.getValue();
			RedisCache redisCache = this.redisCaches.get(dbId);
			if (redisCache == null) {
				redisCache = new RedisCache(redisInfo.getRedisIp(), redisInfo.getRedisPort(),
						serverInfo.getRedisExpireTime(), redisInfo.getRedisPass());
				this.redisCaches.put(dbId, redisCache);
			}
		}
	}

	/**
	 * 初始化mysql数据库并且和表操作的DAO对应
	 * 
	 * @return 
	 * @throws Exception
	 */
	DataSource initDataSource() throws Exception {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setFilters("config");
		Properties properties = new Properties();
		Set<DbInfo> dbInfos = DbMain.serverInfo.getServerDbInfos();
		for (DbInfo dbInfo : dbInfos) {
			dataSource.setUrl(dbInfo.getUrl());
			dataSource.setUsername(dbInfo.getUsername());
			dataSource.setPassword(dbInfo.getPassword());

			dataSource.setInitialSize(dbInfo.getInitilSize());
			dataSource.setMinIdle(dbInfo.getMinIdle());
			dataSource.setMaxActive(dbInfo.getMaxActive());
			dataSource.setValidationQuery(dbInfo.getValidationQuery());
			dataSource.setTestOnBorrow(true);
			dataSource.setMinEvictableIdleTimeMillis(dbInfo.getMinEvictableIdleTimeMillis());
			dataSource.setTimeBetweenEvictionRunsMillis(dbInfo.getTimeBetweenEvictionRunsMillis());
			//TODO : 生产环境需要配置加密
			dataSource.addConnectionProperty(ConfigFilter.CONFIG_DECRYPT, "false");
			dataSource.init();

			RedisCache redisCache = redisCaches.get(dbInfo.getDbId());
			if (redisCache == null) {
				throw new RuntimeException("数据服" + dbInfo.getDbId() + "对应的redis不存在");
			}

			dsIds.add(dbInfo.getDbId());
			
			AutoDao.scan("net.common.po.game", dbInfo.getDbId(), dataSource, redisCache);
		}

		return dataSource;
	}

	/**
	 * 初始化数据中心
	 */
	private void initData() {
		data = new AsyncData();
	}

	/**
	 * 根据数据库id取得redisid
	 * 
	 * @param dbId 
	 * @return
	 */
	public RedisCache getRedisCache(int dbId) {
		return this.redisCaches.get(dbId);
	}

	public Map<Integer, RedisCache> getRedisCaches() {
		return redisCaches;
	}

	public AsyncData getData() {
		return data;
	}

	public Set<Integer> getDsIds() {
		return dsIds;
	}
}
