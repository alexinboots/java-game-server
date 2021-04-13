package net.db.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

/**
 * 基础cache功能
 * 
 * @author ckf
 */
public class RedisCache extends RedisClient {

	public RedisCache(String ip, int port, int expireTime, String password) {
		super(ip, port, expireTime, password);
	}

	public RedisCache(String ip, int port, int maxActive, int maxIdle, int maxWait, int expireTime, String password) {
		super(ip, port, maxActive, maxIdle, maxWait, expireTime, password);
	}

	public boolean check() {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
		} catch (Exception e) {
			isBroken = true;
			logger.error("redis 连接池连接异常:" + e);
			return false;
		} finally {
			returnConnect(jedis, isBroken);
		}

		return true;
	}

	/**
	 * 检查key是否存在
	 */
	public boolean exists(String key) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();

			if (expireTime == 0) {
				return jedis.exists(key);
			} else {
				return jedis.expire(key, expireTime) == 1;
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * multi set
	 * 
	 * @param map
	 */
	public <T> void mset(Map<String, T> map) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			int k = 0;
			String[] strInfos = new String[map.size() * 2];
			for (Entry<String, T> node : map.entrySet()) {
				strInfos[k++] = node.getKey();
				strInfos[k++] = encode(node.getValue());
			}

			jedis = getConnect();

			jedis.mset(strInfos);

			//需要设置过期时间，只能逐一设置
			if (expireTime > 0) {
				for (String key : map.keySet()) {
					jedis.expire(key, expireTime);
				}
			}
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * set
	 */
	public void set(String key, String value) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();

			if (expireTime == 0) {
				jedis.set(key, value);
			} else {
				jedis.setex(key, expireTime, value);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	public void set(String key, Object value, int expireTime) {
		Jedis jedis = null;
		boolean isBroken = false;
		String v = encode(value);
		try {
			jedis = getConnect();

			if (expireTime == 0) {
				jedis.set(key, v);
			} else {
				jedis.setex(key, expireTime, v);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * get
	 */
	public String get(String key) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			return jedis.get(key);

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * add
	 */
	public boolean add(String key, Object value) {
		Jedis jedis = null;
		boolean isBroken = false;
		String v = encode(value);
		String res;
		try {
			jedis = getConnect();
			if (expireTime == 0) {
				res = jedis.set(key, v, RedisClient.NX);
			} else {
				res = jedis.set(key, v, RedisClient.NX, RedisClient.EX, expireTime);
			}
			return res == null ? false : res.equals("OK");
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * update
	 */
	public boolean update(String key, String value) {
		Jedis jedis = null;
		boolean isBroken = false;
		String res;
		try {
			jedis = getConnect();
			if (expireTime == 0) {
				res = jedis.set(key, value);
			} else {
				res = jedis.setex(key, expireTime, value);
			}
			return res.equals("OK");
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * delete
	 */
	public boolean delete(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			Long res = jedis.del(key);
			return res == 1;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * increase
	 */
	public Long increase(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			return jedis.incr(key);
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * decrease
	 */
	public Long decrease(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			return jedis.decr(key);
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * set one key in a map
	 */
	public void mapSet(String key, String subkey, String data) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			jedis.hset(key, subkey, data);
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception, key=" + key + ",subkey=" + subkey, e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * set a map totally
	 */
	public <T> void mapSetAll(String key, Map<String, String> map) {
		mapSetAll(key, map, true);
	}

	/**
	 * set a map totally,deletePrevKey means all reset
	 */
	public <T> void mapSetAll(String key, Map<String, String> map, boolean deletePrevKey) {
		Jedis jedis = null;
		boolean isBroken = false;
		
		try {
			jedis = getConnect();
			if (deletePrevKey) {
				jedis.del(key);
			}
			jedis.hmset(key, map);
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception key=" + key, e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * Delay for one key
	 * 
	 */
	public void mapSetDelay(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * get one key in the map
	 */
	public String mapGet(String key, String subkey) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			return jedis.hget(key, subkey);
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * get a map totally
	 */
	public Map<String, String> mapGetAll(String key) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			return jedis.hgetAll(key);
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * delete one key in map
	 */
	public boolean mapDelete(String key, String subkey) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			Long res = jedis.hdel(key, subkey);
			return res == 1;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * push to the head of list
	 */
	public void listLeftPush(String key, Object obj) {
		Jedis jedis = null;
		boolean isBroken = false;
		String v = encode0(obj);
		try {
			jedis = getConnect();
			jedis.lpush(key, v);
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * push to the tail of list
	 */
	public void listLeftPush(String key, Object obj, int expireTime) {
		Jedis jedis = null;
		boolean isBroken = false;
		String v = encode0(obj);
		try {
			jedis = getConnect();
			jedis.lpush(key, v);
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * push to the tail of list
	 */
	public void listRightPush(String key, Object obj) {
		Jedis jedis = null;
		boolean isBroken = false;
		String v = encode0(obj);
		try {
			jedis = getConnect();
			jedis.rpush(key, v);
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * pop from the head of list
	 */
	public <T> T listLeftPop(String key, Class<T> cls) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			String s = jedis.lpop(key);
			return decode0(s, cls);
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * pop from the head of list
	 * 不指定解析类型,直接返回String
	 * 
	 * @param key
	 * @return
	 */
	public String listRightPop(String key) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			String s = jedis.rpop(key);
			return s;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * pop from the tail of list
	 */
	public <T> T listRightPop(String key, Class<T> cls) {
		Jedis jedis = null;
		boolean isBroken = false;

		try {
			jedis = getConnect();
			String s = jedis.rpop(key);
			return decode0(s, cls);
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * set a list
	 */
	public void listPushAll(String key, List<Object> ls) {
		Jedis jedis = null;
		boolean isBroken = false;
		String[] ls1 = encodeList(ls);
		try {
			jedis = getConnect();
			jedis.del(key);
			jedis.rpush(key, ls1);
			if (expireTime != 0) {
				jedis.expire(key, expireTime);
			}

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * get a range from list
	 */
	public <T> List<T> listRange(String key, int begin, int end, Class<T> cls) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			List<String> ls = jedis.lrange(key, begin, end);
			return decodeList(ls, cls);

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * get length of list
	 */
	public long listLength(String key) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			Long len = jedis.llen(key);
			return len != null ? len.longValue() : 0;

		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * SET if Not eXists
	 * 
	 * @param key
	 * @param value
	 * @return true if the key was set, false if the key was not set
	 */
	public boolean setNX(String key, String value) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			Long ret = jedis.setnx(key, value);
			return ret == 1L;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * Set key to the string value and return the old value stored at key.
	 * 
	 * @param key
	 * @param value
	 * @return the old value stored at key
	 */
	public String getSet(String key, String value) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			String ret = jedis.getSet(key, value);
			return ret;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * Set a timeout on the specified key. After the timeout the key will be
	 * automatically deleted by the server.
	 * 
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Long expire(String key, int seconds) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			Long ret = jedis.expire(key, seconds);
			return ret;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}

	/**
	 * 
	 * EXPIREAT works exctly like EXPIRE but instead to get the number of
	 * seconds representing the Time To Live of the key as a second argument
	 * (that is a relative way of specifing the TTL), it takes an absolute one
	 * in the form of a UNIX timestamp (Number of seconds elapsed since 1 Gen
	 * 1970).
	 * 
	 * @param key
	 * @param unixTime
	 * @return
	 */
	public Long expireAt(String key, long unixTime) {
		Jedis jedis = null;
		boolean isBroken = false;
		try {
			jedis = getConnect();
			Long ret = jedis.expireAt(key, unixTime);
			return ret;
		} catch (RuntimeException e) {
			isBroken = true;
			logger.error("redis exception", e);
			throw e;
		} finally {
			returnConnect(jedis, isBroken);
		}
	}
}
