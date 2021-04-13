package net.db.dao;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.db.BasePo;
import net.db.PoProxy;
import net.db.anno.Cacheable;

/**
 * 缓存Dao
 *
 * @author ckf
 * @see Cacheable
 */
public class CachedDao<T extends BasePo> extends Dao<T> {

	private Logger logger = LoggerFactory.getLogger(CachedDao.class);

	/**
	 * 是否Key映射到多份数据
	 */
	protected boolean isMany;

	/**
	 * key的属性
	 */
	protected Field keyField;

	/**
	 * subkey的属性
	 */
	protected Field subkeyField;

	/**
	 * 通过key取得多份数据的sql
	 */
	protected String manyInitSql;

	/**
	 * 缓存工具
	 */
	protected RedisCache cache;

	public CachedDao(Class<T> cls, PoProxy poProxy, RedisCache cache) {
		super(cls, poProxy);
		try {
			Cacheable cacheable = cls.getAnnotation(Cacheable.class);
			this.manyInitSql = cacheable.manyInitSql();
			this.keyField = cls.getSuperclass().getDeclaredField(cacheable.key());
			this.keyField.setAccessible(true);
			if (cacheable.subkey().length() > 0) {
				this.isMany = true;
				this.subkeyField = cls.getSuperclass().getDeclaredField(cacheable.subkey());
				this.subkeyField.setAccessible(true);
			}
			this.cache = cache;
		} catch (Exception e) {
			logger.error("!!!", e);
		}
	}

	@Deprecated @Override public String findByIdFromDb(Object... ids) {
		throw new RuntimeException("findById is blocked in CachedDao!");
	}

	@Override public String findByProp(String[] propName, Object[] values) {
		logger.warn("findByProp is not recommended in CachedDao!");
		return super.findByProp(propName, values);
	}

	@Override public int deleteById(Object... ids) {
		throw new RuntimeException("deleteById is blocked in CachedDao!");
	}

	@Deprecated @Override public int deleteByProp(String[] propName, Object[] values) {
		throw new RuntimeException("deleteByProp is blocked in CachedDao!");
	}

	@Deprecated @Override public int deleteBySQL(String sql, Object[] values) {
		throw new RuntimeException("deleteBySQL is blocked in CachedDao!");
	}

	@Override public int delete(String data) {
		T t = decode(data);
		if (isMany) {
			String key = getKey(t);
			String subkey = getSubkey(t);
			cache.mapDelete(key, subkey);
		} else {
			String key = getKey(t);
			cache.delete(key);
		}

		return super.deleteById(t.idValues());
	}

	@Override
	public boolean update(String data) {
		T t = decode(data);
		if (isMany) {

			check(t);

			String key = getKey(t);
			String subkey = getSubkey(t);

			try {
				cache.mapSet(key, subkey, data);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		} else {
			String key = getKey(t);

			try {
				if (!cache.update(key, data)) {
					return false;
				}
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		return super.update(t);
	}

	@Override
	public void insert(String data) throws SQLException {
		T t = decode(data);
		super.insert(t);

		if (isMany) {

			check(t);

			String key = getKey(t);
			String subkey = getSubkey(t);

			try {
				cache.mapSet(key, subkey, data);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		} else {
			String key = getKey(t);

			try {
				cache.add(key, t);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}
	}

	@Override public void insertBatch(List<String> dataList) throws SQLException {
		if (dataList.size() == 0) {
			return;
		}

		super.insertBatch(dataList);

		String tmp = dataList.get(0);
		T tmpT = decode(tmp);
		if (isMany) {
			String key = this.getKey(tmpT);
			boolean isExist = cache.exists(key);

			if (isExist) {
				Map<String, String> map = new HashMap<>();
				for (String data : dataList) {
					T t = decode(data);
					map.put(getSubkey(t), data);
				}
				//只进行追加
				cache.mapSetAll(key, map, false);
			} else {
				//全部捞入redis
				check(tmpT);
			}
		} else {
			Map<String, String> map = new HashMap<>();
			for (String data : dataList) {
				T t = decode(data);
				map.put(getSubkey(t), data);
			}
			//全部设置到redis
			cache.mset(map);
		}
	}

	/**
	 * 一对多关系中涉及主key更新的update
	 */
	public void updateWithKey(String data, Object oldId) {
		if (!isMany) {
			throw new RuntimeException("updateWithKey is blocked in CacheType ONE!");
		}

		T t = decode(data);
		check(t);

		String oldKey = getKey(oldId);
		String newKey = getKey(t);
		String subkey = getSubkey(t);

		cache.mapDelete(oldKey, subkey);
		cache.mapSet(newKey, subkey, data);

		super.update(t);
	}

	@Override public int execute(String sql, Object... params) throws SQLException {
		int ret = super.execute(sql, params);
		logger.warn("execute is not recommended in CachedDao!");
		return ret;
	}

	/**
	 * 通过id获取一条数据，适用于CacheType.ONE
	 * 强制从数据库刷新缓存
	 *
	 * @param id
	 * @return
	 */
	public String getFromDb(Object id) {
		if (isMany) {
			throw new RuntimeException("get is blocked in CacheType MANY!");
		}
		// 从数据库load数据
		T t = super.findById(id);
		String r = encode(t);
		if (r != null) {
			logger.debug("load data from db, key=" + getKey(t));
			try {
				cache.set(getKey(t), r);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		return r;
	}

	/**
	 * 通过id获取一条数据，适用于CacheType.ONE
	 *
	 * @param id
	 * @return
	 */
	public String get(Object id) {
		if (isMany) {
			throw new RuntimeException("get is blocked in CacheType MANY!");
		}

		String r = null;
		try {
			r = cache.get(getKey(id));
		} catch (RuntimeException e) {
			logger.error("cache error", e);
		}

		// 从数据库load数据
		if (r == null) {
			T t = super.findById(id);
			if (t != null) {
				logger.debug("load data from db, key=" + getKey(t));
				try {
					cache.set(getKey(t), encode(t));
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}

			r = encode(t);
		}

		return r;
	}

	/**
	 * 通过id获取一组数据，适用于CacheType.MANY
	 * 强制刷新缓存
	 *
	 * @param id
	 * @return
	 */
	public String getListFromDb(Object id) {
		Map<String, String> map = new HashMap<>();
		List<T> ls = super.findBySQL(manyInitSql, new Object[] { id });
		if (ls != null && ls.size() > 0) {
			logger.debug("load data from db, key=" + getKey(id));

			for (T t : ls) {
				map.put(getSubkey(t), encode(t));
			}
			try {
				cache.mapSetAll(getKey(id), map);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		}

		return encode(map.values());
	}

	/**
	 * 通过id获取一组数据，适用于CacheType.MANY
	 *
	 * @param id
	 * @return
	 */
	public String getList(Object id) {
		if (!isMany) {
			throw new RuntimeException("getList is blocked in CacheType ONE!");
		}

		Map<String, String> map = null;
		try {
			map = cache.mapGetAll(getKey(id));
		} catch (RuntimeException e) {
			logger.error("cache error", e);
		}

		// 从数据库load数据
		if (map == null || map.size() == 0) {
			map = new HashMap<>();
			List<T> ls = super.findBySQL(manyInitSql, new Object[] { id });
			if (ls != null && ls.size() > 0) {
				logger.debug("load data from db, key=" + getKey(id));

				for (T t : ls) {
					map.put(getSubkey(t), encode(t));
				}
				try {
					cache.mapSetAll(getKey(id), map);
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}
		} else {
			cache.mapSetDelay(getKey(id));//自动延期
		}

		return encode(map.values());
	}

	/**
	 * 从一对多关系中获取一条数据
	 *
	 * @param id
	 * @param subId
	 * @return
	 */
	public String getOne(Object id, Object subId, boolean isFlushCache) {
		if (!isMany) {
			throw new RuntimeException("getList is blocked in CacheType ONE!");
		}

		String key = getKey(id);
		String subkey = getSubkey(subId);

		boolean exists = false;
		try {
			exists = cache.exists(key);
			if (exists) {
				String r = cache.mapGet(key, subkey);
				if (r != null) {
					return r;
				}

				//否则就从数据库查一次
			}
		} catch (RuntimeException e) {
			logger.error("cache error", e);
		}
		// 从数据库load数据
		if (isFlushCache) {
			HashMap<String, String> map = new HashMap<>();
			List<T> ls = super.findBySQL(manyInitSql, new Object[] { id });
			if (ls != null && ls.size() > 0) {
				logger.debug("load data from db, key=" + getKey(id));
				for (T t : ls) {
					map.put(getSubkey(t), encode(t));
				}

				try {
					cache.mapSetAll(getKey(id), map);
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}

			return map.get(getSubkey(subId));

		} else {
			return encode(super.findById(subId));

		}
	}

	/**
	 * 异步删除 1.删除缓存
	 *
	 * @param t
	 * @return
	 */
	public int asyncDelete(T t) {
		if (isMany) {
			String key = getKey(t);
			String subkey = getSubkey(t);
			return cache.mapDelete(key, subkey) ? 1 : 0;
		} else {
			String key = getKey(t);
			return cache.delete(key) ? 1 : 0;
		}
	}

	/**
	 * 异步删除 2.删除数据库
	 *
	 * @param t
	 * @return
	 */
	public int asyncDeleteDb(T t) {
		return super.deleteById(t.idValues());
	}

	/**
	 * 异步更新 1.更新缓存
	 *
	 * @param data
	 * @return
	 */
	public boolean asyncUpdate(String data) {
		T t = decode(data);
		if (isMany) {

			check(t);

			String key = getKey(t);
			String subkey = getSubkey(t);

			cache.mapSet(key, subkey, data);
		} else {
			String key = getKey(t);

			if (!cache.update(key, data)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 异步更新 
	 *
	 * @param t
	 * @return
	 */
	public boolean asyncUpdateDb(T t) {
		return super.update(t);
	}

	/**
	 * 异步 一对多关系中涉及主key更新的update 1. 更新缓存
	 */
	public void asyncUpdateWithKey(String data, Object oldId) {
		if (!isMany) {
			throw new RuntimeException("asyncUpdateWithKey1 is blocked in CacheType ONE!");
		}

		T t = decode(data);
		check(t);

		String oldKey = getKey(oldId);
		String newKey = getKey(t);
		String subkey = getSubkey(t);

		cache.mapDelete(oldKey, subkey);
		cache.mapSet(newKey, subkey, data);
	}

	protected String getKey(T obj) {
		try {
			return cls.getSimpleName() + "_" + keyField.get(obj);
		} catch (IllegalAccessException e) {
			logger.error("!!!", e);
		}

		return "";
	}

	protected String getSubkey(T obj) {
		if (isMany) {
			try {
				return String.valueOf(subkeyField.get(obj));
			} catch (IllegalAccessException e) {
				logger.error("!!!", e);
			}
		}
		return "";
	}

	protected String getKey(Object id) {
		return cls.getSimpleName() + "_" + id;
	}

	protected String getSubkey(Object subId) {
		return String.valueOf(subId);
	}

	protected String getMarkKey(Object... ids) {
		if (isMany) {
			if (ids.length != 2) {
				throw new RuntimeException("must give key and subkey");
			}

			String key = getKey(ids[0]);
			String subkey = getSubkey(ids[1]);
			return key + subkey;
		} else {
			if (ids.length != 1) {
				throw new RuntimeException("must give key");
			}

			String key = getKey(ids[0]);
			return key;
		}
	}

	protected String getMarkKey(T t) {
		if (isMany) {
			String key = getKey(t);
			String subkey = getSubkey(t);

			return key + subkey;
		} else {
			String key = getKey(t);

			return key;
		}
	}

	protected String getOldMarkKey(Object oldId, T t) {
		if (isMany) {
			String key = getKey(oldId);
			String subkey = getSubkey(t);

			return key + subkey;
		} else {
			throw new RuntimeException("one to one unsupperted getOldMarkKey");
		}
	}

	public void setCache(RedisCache cache) {
		this.cache = cache;
	}

	/**
	 * 延期处理
	 *
	 * @param id 对象id
	 */
	public void listDelay(Object id) {
		cache.mapSetDelay(getKey(id));
	}

	/**
	 * 用于检查List在缓存中是否存在，不存在则从数据库捞
	 *
	 * @param t
	 */
	public void check(T t) {
		if (!isMany) {
			return;
		}
		String key = getKey(t);
		boolean isExist = cache.exists(key);

		if (!isExist) { //不存在可能被过期移除了，需要重新从数据库捞一遍
			Object id = null;
			try {
				id = keyField.get(t);
			} catch (IllegalAccessException e) {
				logger.error("CacheDao.check", e);
				return;
			}
			Map<String, String> map = new HashMap<>();
			List<T> ls = super.findBySQL(manyInitSql, new Object[] { id });
			if (ls != null && ls.size() > 0) {
				logger.debug("load data from db, key=" + getKey(id));

				for (T t1 : ls) {
					map.put(getSubkey(t1), encode(t1));
				}
				try {
					cache.mapSetAll(key, map);
				} catch (RuntimeException e) {
					logger.error("cache error", e);
				}
			}
		}
	}

	/**
	 * 重新加载列表的数据到缓存
	 *
	 * @param id
	 * @return
	 */
	private List<T> reloadListToCache(Object id) {
		List<T> ls = super.findBySQL(manyInitSql, new Object[] { id });
		if (ls != null && ls.size() > 0) {
			logger.debug("load data from db, key=" + getKey(id));

			String key = getKey(ls.get(0));

			Map<String, String> map = new HashMap<>();
			for (T t1 : ls) {
				map.put(getSubkey(t1), encode(t1));
			}
			try {
				cache.mapSetAll(key, map);
			} catch (RuntimeException e) {
				logger.error("cache error", e);
			}
		} else if (ls == null) {
			ls = new ArrayList<T>();
		}
		return ls;
	}
}
