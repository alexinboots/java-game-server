package net.db.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import net.db.BasePo;
import net.db.PoProxy;
import net.db.sqlhandler.SqlHandlerType;
import net.utils.JsonUtils;

/**
 * 数据库工具
 *
 * @author ckf
 */
public abstract class Dao<T extends BasePo> {

	private static Logger logger = LoggerFactory.getLogger(Dao.class);

	/**
	 * 所有数据类对应的数据操作工具
	 */
	protected static Map<Integer, Map<String, Dao>> ALL_DAOS = new HashMap<>();

	/**
	 * 查询结果处理器
	 */
	protected Map<Integer, ResultSetHandler> RESULT_HANDLERS = new HashMap<>();
	
	/**
	 * Some drivers don't support {@link java.sql.ParameterMetaData#getParameterType(int) };
	 * if <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it,
	 * and if it breaks, we'll remember not to use it again.
	 */
	protected final static boolean PMD_KNOWN_BROKEN = true;

	/**
	 * po对应的java类
	 */
	protected final Class<T> cls;

	/**
	 * 查询结果处理器
	 */
	protected final BeanHandler<T> beanHandler;

	/**
	 * 查询结果集合处理器
	 */
	protected final BeanListHandler<T> beanListHandler;

	/**
	 * po操作代理
	 */
	protected final PoProxy poProxy;

	public static void initDaoMap(int dsid, Map<Class, Dao> daoMap) {
		Map<String, Dao> existsMap = ALL_DAOS.get(dsid);

		if (existsMap == null) {
			existsMap = new HashMap<>(daoMap.size());
			ALL_DAOS.put(dsid, existsMap);
		}

		for (Entry<Class, Dao> node : daoMap.entrySet()) {
			if (existsMap.containsKey(node.getKey())) {
				throw new RuntimeException("出现重复类型 po = " + node.getKey().getSimpleName());
			}

			existsMap.put(node.getKey().getSimpleName(), node.getValue());
		}
	}

	public static <T extends BasePo> Dao<T> getDao(int dsid, String className) {
		Map<String, Dao> daos = ALL_DAOS.get(dsid);
		Dao dao = daos.get(className);
		return dao;
	}

	/**
	 * 用于通过一个dbserver管理多个mysql源
	 *
	 * @param cls
	 * @param proxy
	 */
	public Dao(Class<T> cls, PoProxy proxy) {
		this.cls = cls;
		this.beanHandler = new BeanHandler<>(cls);
		this.beanListHandler = new BeanListHandler<>(cls);
		this.poProxy = proxy;
	}

	/**
	 * 通过id查找数据
	 *
	 * @param ids
	 * @return
	 */
	public T findById(Object... ids) {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			T data = qr.query(poProxy.select, beanHandler, ids);
			return data;
		} catch (Exception e) {
			logger.error("findById ERROR!!!", e);
		}
		return null;
	}

	public String findAll() {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			List<T> tList = qr.query(poProxy.selectAll, beanListHandler);
			return encode(tList);
		} catch (Exception e) {
			logger.error("findAll ERROR!!!", e);
		}
		
		return null;
	}

	/**
	 * 通过指定属性值查找数据
	 *
	 * @param propNames
	 * @param values
	 * @return
	 */
	public String findByProp(String[] propNames, Object[] values) {
		try {
			StringBuilder sb = new StringBuilder(poProxy.selectAll);
			sb.append(" WHERE ");
			sb.append(propNames[0]).append("=").append("?");
			for (int i = 1; i < propNames.length; ++i) {
				sb.append(" and ").append(propNames[i]).append("=?");
			}
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			List<T> tList = qr.query(sb.toString(), beanListHandler, values);
			return encode(tList);
		} catch (Exception e) {
			logger.error("findByProp ERROR!!!", e);
		}

		return null;
	}

	/**
	 * 通过sql查找数据
	 *
	 * @param sql 只需条件部分
	 */
	public List<T> findBySQL(String sql, Object[] values) {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			List<T> tList = qr.query(poProxy.selectAll + " WHERE " + sql, beanListHandler, values);
			return tList;
		} catch (Exception e) {
			logger.error("findBySQL ERROR!!!", e);
		}

		return null;
	}

	/**
	 * 通过id删除数据
	 *
	 * @param ids
	 * @return
	 */
	public int deleteById(Object... ids) {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			int ret = qr.update(poProxy.delete, ids);
			return ret;
		} catch (Exception e) {
			logger.error("deleteById ERROR!!!", e);
		}

		return 0;
	}

	/**
	 * 通过指定值删除数据
	 *
	 * @param propName
	 * @param values
	 * @return
	 */
	public int deleteByProp(String[] propName, Object[] values) {
		try {
			StringBuilder sb = new StringBuilder(poProxy.deleteAll);
			sb.append(" WHERE ");
			sb.append(propName[0]).append("=").append("?");
			for (int i = 1; i < propName.length; ++i) {
				sb.append(" and ").append(propName[i]).append("=?");
			}
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			int ret = qr.update(sb.toString(), values);
			return ret;
		} catch (Exception e) {
			logger.error("deleteByProp ERROR!!!", e);
		}
		return 0;
	}

	/**
	 * 通过sql删除数据
	 *
	 * @param sql    只需条件值部分
	 * @param values
	 * @return
	 */
	public int deleteBySQL(String sql, Object[] values) {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			int ret = qr.update(poProxy.deleteAll + " WHERE " + sql, values);
			return ret;
		} catch (Exception e) {
			logger.error("deleteBySQL ERROR!!!", e);
			return 0;
		}
	}
	
	/**
	 * 删除数据
	 *
	 * @param data
	 * @return
	 */
	public int delete(String data) {
		return deleteById(this.decode(data));
	}

	/**
	 * 更新数据
	 *
	 * @param data
	 * @return
	 */
	public boolean update(String data) {
		T t = decode(data);
		if (t == null) {
			return false;
		}

		return this.update(t);
	}


	/**
	 * 插入数据
	 *
	 * @param data
	 * @throws SQLException
	 */
	public void insert(String data) throws SQLException {
		insert(this.decode(data));
	}

	/**
	 * 更新数据
	 */
	public boolean update(T t) {
		try {
			Object[] props = t.propValues();
			Object[] ids = t.idValues();
			Object[] objects = new Object[props.length + ids.length];

			System.arraycopy(props, 0, objects, 0, props.length);
			System.arraycopy(ids, 0, objects, props.length, ids.length);

			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			int ret = qr.update(poProxy.update, objects);
			return ret > 0;
		} catch (Exception e) {
			logger.error("update ERROR!!!", e);
		}
		return false;
	}

	/**
	 * 插入数据
	 *
	 * @param t
	 * @throws SQLException
	 */
	public void insert(T t) throws SQLException {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			qr.update(poProxy.insert, t.propValues());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 批量插入
	 *
	 * @param dataList
	 * @throws SQLException
	 */
	public void insertBatch(List<String> dataList) throws SQLException {
		if (dataList.size() == 0) {
			return;
		}

		try {
			Object[][] params = new Object[dataList.size()][];
			for (int i = 0; i < params.length; i++) {
				String data = dataList.get(i);
				T t = decode(data);
				params[i] = t.propValues();
			}
			
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			qr.batch(poProxy.insert, params);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 执行sql
	 *
	 * @param sql
	 * @param params
	 * @return The number of rows updated.
	 * @throws SQLException
	 */
	public int execute(String sql, Object... params) throws SQLException {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			int ret = qr.update(sql, params);
			return ret;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 通过sql查询
	 *
	 * @param sql
	 * @param handler
	 * @param params
	 * @param <V>
	 * @return
	 * @throws SQLException
	 */
	public <V> V query(String sql, ResultSetHandler<V> handler, Object... params) throws SQLException {
		try {
			QueryRunner qr = new QueryRunner(poProxy.ds, PMD_KNOWN_BROKEN);
			V result = qr.query(sql, handler, params);
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 通过sql查找数据
	 *
	 * @param sql 只需条件部分
	 */
	public String findBySQLFromDb(String sql, Object[] values) {
		return this.encode(findBySQL(sql, values));
	}

	/**
	 * 通过sql查询
	 *
	 * @param sql
	 * @param handlerType
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public String queryFromDb(String sql, SqlHandlerType handlerType, Object... params) throws SQLException {
		ResultSetHandler resultSetHandler = RESULT_HANDLERS.get(handlerType);
		return encode(query(sql, resultSetHandler, params));
	}
	
	/**
	 * 通过id查找数据
	 *
	 * @param ids
	 * @return
	 */
	public String findByIdFromDb(Object... ids) {
		return this.encode(findById(ids));
	}

	public String encode(Object object) {
		return JsonUtils.object2String(object);
	}

	public T decode(String data) {
		return JsonUtils.string2Object(data, cls);
	}

	public List<T> decode2List(String data) {
		TypeReference<List<T>> type = new TypeReference<List<T>>() {
		};
		return JsonUtils.string2Object(data, type);
	}
}
