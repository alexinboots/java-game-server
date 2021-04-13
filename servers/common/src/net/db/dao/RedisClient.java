package net.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis客户端包装器
 * 
 * @author ckf
 */
public abstract class RedisClient {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected JedisPool pool;
	
	/**
	 * 对象池的大小
	 */
	private final static int MAX_ACTIVE = 1000;
	
	/**
	 * 最大保持空闲状态的对象
	 */
	private final static int MAX_IDLE = 100;
	
	/**
	 * 获取池内对象最大等待时间
	 */
	private final static int MAX_WAIT = 1000;
	
	/**
	 * 当调用borrow Object方法时，是否进行有效性检查
	 */
	private final static boolean TEST_ON_BORROW = true;
	
	/**
	 * 当调用return Object方法时，是否进行有效性检查
	 */
	private final static boolean TEST_ON_RETURN = true;

	/**
	 * 连接超时设置（毫秒）
	 */
	private final static int TIME_OUT = 2000;

	/**
	 * 连接的ip
	 */
	private String host;

	/**
	 * 连接的端口
	 */
	private int port;

	/**
	 * 过期时间，为0不过期
	 */
	protected int expireTime = 0;

	/**
	 * ok
	 */
	public static final String OK = "OK";

	/**
	 * 不存在
	 */
	public static final String NX = "NX";

	/**
	 * 存在
	 */
	public static final String XX = "XX";

	/**
	 * 以秒为过期单位
	 */
	public static final String EX = "EX";

	/**
	 * 以毫秒为过期单位
	 */
	public static final String PX = "PX";

	/**
	 * 使用默认配置创建Redis连接
	 *
	 * @param ip
	 *            redis-ip
	 * @param port
	 *            redis-port
	 * @param expireTime
	 *            过期时间
	 * @param password
	 */
	public RedisClient(String ip, int port, int expireTime, String password) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(MAX_ACTIVE);
		config.setMaxIdle(MAX_IDLE);
		config.setMaxWaitMillis(MAX_WAIT);
		config.setTestOnBorrow(TEST_ON_BORROW);
		config.setTestOnReturn(TEST_ON_RETURN);

		if (password == null || password.length() < 1) {
			pool = new JedisPool(config, ip, port, TIME_OUT);
		} else {
			pool = new JedisPool(config, ip, port, TIME_OUT, password);
		}

		this.expireTime = expireTime;
		this.host = ip;
		this.port = port;
	}

	/**
	 * 使用自定义配置创建Redis连接
	 *
	 * @param ip
	 *            redis-ip
	 * @param port
	 *            redis-port
	 * @param maxActive
	 *            对象池的大小
	 * @param maxIdle
	 *            最大保持空闲状态的对象
	 * @param maxWait
	 *            获取jedis对象最长等待时间(ms)
	 * @param expireTime
	 *            过期时间
	 * @param password
	 */
	public RedisClient(String ip, int port, int maxActive, int maxIdle, int maxWait, int expireTime, String password) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxActive);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWait);
		config.setTestOnBorrow(TEST_ON_BORROW);
		config.setTestOnReturn(TEST_ON_RETURN);

		if (password == null || password.length() < 1) {
			pool = new JedisPool(config, ip, port, TIME_OUT);
		} else {
			pool = new JedisPool(config, ip, port, TIME_OUT, password);
		}

		this.expireTime = expireTime;
		this.host = ip;
		this.port = port;
	}

	/**
	 * 获取连接的ip
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * 获取当前连接的端口
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 从对象池获得一个redis连接
	 *
	 * @return
	 */
	protected Jedis getConnect() {
		return pool.getResource();
	}

	/**
	 * 向对象池返回一个使用完毕的连接
	 *
	 * @param conn
	 */
	protected void returnConnect(Jedis conn, boolean isBroken) {
		if (conn != null) {
			if (isBroken) {
				pool.returnBrokenResource(conn);
			} else {
				pool.returnResource(conn);
			}
		}
	}

	/**
	 * 清理redis
	 */
	@Deprecated
	public void flushAll() {

		Jedis jedis = null;

		boolean isBroken = false;
		try {
			jedis = getConnect();
			jedis.flushAll();
		} catch (Exception e) {
			isBroken = true;
			logger.error(e.getMessage(), e);
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * 简单序列化
	 */
	protected String encode0(Object o) {
		return JSON.toJSONString(o, SerializerFeature.WriteClassName);
	}

	/**
	 * 简单反序列化
	 */
	protected <T> T decode0(String s, Class<T> cls) {
		return JSON.parseObject(s, cls);
	}

	/**
	 * 序列化
	 */
	protected String encode(Object o) {
		if (o instanceof String) {
			return (String) o;
		} else if (o instanceof Integer) {
			return o.toString();
		} else {
			return JSON.toJSONString(o, SerializerFeature.WriteClassName);
		}
	}

	/**
	 * 反序列化
	 */
	protected <T> T decode(String s, Class<T> cls) {
		if (s == null) {
			return null;
		}

		if (cls == String.class) {
			return (T) s;
		} else if (cls == Integer.class || cls == int.class) {
			return (T) Integer.valueOf(s);
		} else {
			return JSON.parseObject(s, cls);
		}
	}

	/**
	 * 序列化数组
	 */
	protected String[] encodeList(List<Object> ls) {
		String[] res = new String[ls.size()];
		for (int i = 0; i < ls.size(); ++i) {
			res[i] = encode0(ls.get(i));
		}
		return res;
	}

	/**
	 * 反序列化数组
	 */
	protected <T> List<T> decodeList(List<String> ls, Class<T> cls) {
		List<T> res = new ArrayList<>();
		for (String s : ls) {
			res.add(decode0(s, cls));
		}
		return res;
	}

	/**
	 * 序列化map
	 */
	protected <T> Map<String, String> encodeMap(Map<String, T> map) {
		if (map == null) {
			return null;
		}

		Map<String, String> res = new HashMap<>();
		for (Map.Entry<String, T> entry : map.entrySet()) {
			String k = entry.getKey();
			T v = entry.getValue();

			res.put(k, encode0(v));
		}

		return res;
	}

	/**
	 * 反序列化map
	 */
	protected <T> Map<String, T> decodeMap(Map<String, String> map, Class<T> cls) {
		if (map == null) {
			return null;
		}

		Map<String, T> res = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String k = entry.getKey();
			String v = entry.getValue();

			res.put(k, decode0(v, cls));
		}

		return res;
	}
}
