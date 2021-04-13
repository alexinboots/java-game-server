package net.utils.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import net.db.dao.Dao;
import net.db.PoProxy;
import net.db.anno.Cacheable;
import net.db.dao.CachedDao;
import net.db.dao.DbDao;
import net.db.dao.RedisCache;
import net.utils.ClassPathScanner;

/**
 * 数据表对应的dao生成工具
 *
 * @author ckf
 */
public class AutoDao {

	@SuppressWarnings("rawtypes")
	public static Set<Class> scanPo(String path) {
		Set<Class> classes = ClassPathScanner.scan(path, false, true, false, null);
		return classes;
	}

	@SuppressWarnings("rawtypes")
	public static void scan(String path, int dsId, DataSource ds, RedisCache cache) {
		Set<Class> classes = ClassPathScanner.scan(path, false, true, false, null);

		scan(classes, dsId, ds, cache);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void scan(Set<Class> classes, int dsId, DataSource ds, RedisCache cache) {
		Map<Class, PoProxy> tables = PoRegister.scan(classes, ds);

		Map<Class, Dao> daoMap = new HashMap<>();
		for (Entry<Class, PoProxy> node : tables.entrySet()) {
			Class cls = node.getKey();
			PoProxy table = node.getValue();

			Dao dao;
			if (cls.getAnnotation(Cacheable.class) != null) {
				dao = new CachedDao(cls, table, cache);
			} else {
				dao = new DbDao(cls, table);
			}

			daoMap.put(cls, dao);
		}

		Dao.initDaoMap(dsId, daoMap);
	}

}
