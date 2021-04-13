package net.db.dao;

import java.sql.SQLException;
import java.util.List;
import net.db.BasePo;
import net.db.Data;
import net.db.sqlhandler.SqlHandlerType;
import net.utils.SpringContext;

/**
 * 同步版数据操作
 * 
 * @author chao
 */
public class SyncData implements Data {

	@Override public String findById(int dsId, String className, Object... ids) {
		return Dao.getDao(dsId, className).findByIdFromDb(ids);
	}

	@Override public String findAll(int dsId, String className) {
		return Dao.getDao(dsId, className).findAll();
	}

	@Override public String findByProp(int dsId, String className, String[] props, Object[] values) {
		return Dao.getDao(dsId, className).findByProp(props, values);
	}

	@Override public String findBySQL(int dsId, String className, String sql, Object[] values) {
		return Dao.getDao(dsId, className).findBySQLFromDb(sql, values);
	}

	@Override public int execute(int dsId, String className, String sql, Object[] values) throws SQLException {
		return Dao.getDao(dsId, className).execute(sql, values);
	}

	@Override public String query(int dsId, String className, String sql, SqlHandlerType handlerType,
			Object... params) throws SQLException {
		return Dao.getDao(dsId, className).queryFromDb(sql, handlerType, params);
	}

	@Override public int deleteById(int dsId, String className, Object... ids) {
		return Dao.getDao(dsId, className).deleteById(ids);
	}

	@Override public int deleteByProp(int dsId, String className, String[] propName, Object[] values) {
		return Dao.getDao(dsId, className).deleteByProp(propName, values);
	}

	@Override public int deleteBySQL(int dsId, String className, String sql, Object[] values) {
		return Dao.getDao(dsId, className).deleteBySQL(sql, values);
	}

	@Override public <T extends BasePo> int delete(int dsId, String className, String data) {
		return Dao.getDao(dsId, className).delete(data);
	}

	@Override public  <T extends BasePo> boolean update(int dsId, String className, String data) {
		return Dao.getDao(dsId, className).update(data);
	}

	@Override public void insert(int dsId, String className, String data) throws SQLException {
		Dao.getDao(dsId, className).insert(data);
	}

	@Override public <T extends BasePo> String get(int dsId, String className, Object id) {
		return ((CachedDao<T>) Dao.getDao(dsId, className)).get(id);
	}

	@Override public <T extends BasePo> String getList(int dsId, String className, Object id) {
		return ((CachedDao<T>) Dao.getDao(dsId, className)).getList(id);
	}

	@Override public <T extends BasePo> String getOne(int dsId, String className, Object id, Object subId, boolean isFlushCache) {
		return ((CachedDao<T>) Dao.getDao(dsId, className)).getOne(id, subId, isFlushCache);
	}

	@Override public <T extends BasePo> String getFromDb(int dsId, String className, Object id) {
		return ((CachedDao<T>) Dao.getDao(dsId, className)).getFromDb(id);
	}

	@Override public <T extends BasePo> String getListFromDb(int dsId, String className, Object id) {
		return ((CachedDao<T>) Dao.getDao(dsId, className)).getListFromDb(id);
	}

	@Override public boolean patrol() {
		RedisCache redis = SpringContext.getBean(RedisCache.class);
		boolean isRunning = redis.check();
		if (!isRunning) {
			return false;
		} else {
			return true;
		}
	}

	@Override public <T extends BasePo> void listDelay(int dsId, String className, Object id) {
		((CachedDao<T>) Dao.getDao(dsId, className)).listDelay(id);
	}

	@Override public void insertBatch(int dsId, String className, List<String> dataList) throws SQLException {
		Dao.getDao(dsId, className).insertBatch(dataList);
	}
}
