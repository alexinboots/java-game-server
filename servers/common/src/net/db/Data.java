package net.db;

import java.sql.SQLException;
import java.util.List;

import net.db.sqlhandler.SqlHandlerType;

/**
 * 数据接口
 *
 * @author ckf
 */
public interface Data {

	/**
	 * 非缓存类访问:通过id获取
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param ids
	 * @return
	 */
	String findById(int dsId, String className, Object... ids);

	/**
	 * 非缓存类访问:获取全表
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @return
	 */
	String findAll(int dsId, String className);

	/**
	 * 非缓存类访问:通过属性查找
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param props
	 * @param values
	 * @return
	 */
	String findByProp(int dsId, String className, String[] props, Object[] values);

	/**
	 * 非缓存类访问:通过sql查找
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param sql
	 * @param values
	 * @return
	 */
	String findBySQL(int dsId, String className, String sql, Object[] values);

	/**
	 * 非缓存类访问:执行sql
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param sql
	 * @param values
	 * @return The number of rows updated.
	 */
	int execute(int dsId, String className, String sql, Object[] values) throws SQLException;

	/**
	 * 非缓存类访问:查询sql
	 *
	 * @param dsId
	 *            数据源id
	 * @param sql
	 *            需要整条sql,示例 "select max(id) from xxx where aaa=? and bbb=?"
	 * @param handler
	 *            <a href=
	 *            "http://commons.apache.org/proper/commons-dbutils/examples.html">用法看这里</a>
	 */
	String query(int dsId, String className, String sql, SqlHandlerType handlerType, Object... params)
			throws SQLException;

	/**
	 * 非缓存类访问:删除
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param ids
	 * @return 受影响的行数
	 */
	int deleteById(int dsId, String className, Object... ids);

	/**
	 * 非缓存类访问:通过属性删除
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param propName
	 * @param values
	 * @return 受影响的行数
	 */
	int deleteByProp(int dsId, String className, String[] propName, Object[] values);

	/**
	 * 非缓存类访问:通过sql删除
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param sql
	 * @param values
	 * @return 受影响的行数
	 */
	int deleteBySQL(int dsId, String className, String sql, Object[] values);

	/**
	 * 通用访问:删除
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param data
	 * @return
	 */
	<T extends BasePo> int delete(int dsId, String className, String data);

	/**
	 * 通用访问:更新
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param data
	 * @return
	 */
	<T extends BasePo> boolean update(int dsId, String className, String data);

	/**
	 * 通用访问:插入，不支持自动主键
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @return
	 */
	void insert(int dsId, String className, String data) throws SQLException;

	/**
	 * 缓存类访问:获取一对一关系中的一条数据
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param id
	 *            key
	 * @return
	 */
	<T extends BasePo> String get(int dsId, String className, Object id);

	/**
	 * 缓存类访问:获取一对多关系中的一组数据<br>
	 * 可以理解为 Map[String:Map] 中的map.get()
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param id
	 *            一级key
	 * @return
	 */
	<T extends BasePo> String getList(int dsId, String className, Object id);

	/**
	 * 缓存类访问:获取一对多关系中的一个数据<br>
	 * 不在缓存中时会尝试查询db
	 * 可以理解为 Map[String:Map] 中的map.get().get()
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param id
	 *            一级key
	 * @param subId
	 *            二级key
	 * @param isFlushCache
	 *            尝试查询db后是否同时刷新缓存
	 * @return
	 */
	<T extends BasePo> String getOne(int dsId, String className, Object id, Object subId, boolean isFlushCache);

	/**
	 * 缓存类访问:获取一对一关系中的一条数据
	 * 强制刷新缓存
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param id
	 *            key
	 * @return
	 */
	<T extends BasePo> String getFromDb(int dsId, String className, Object id);

	/**
	 * 缓存类访问:获取一对多关系中的一组数据<br>
	 * 可以理解为 Map[String:Map] 中的map.get()
	 * 强制刷新缓存
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param id
	 *            一级key
	 * @return
	 */
	<T extends BasePo> String getListFromDb(int dsId, String className, Object id);

	/**
	 * 巡查redis
	 *
	 * @return
	 */
	boolean patrol();

	/**
	 * 对列表缓存做延期
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @param id
	 */
	<T extends BasePo> void listDelay(int dsId, String className, Object id);

	/**
	 * 通用访问:批量插入，不支持自动主键
	 *
	 * @param dsId
	 *            数据源id
	 * @param className
	 * @return
	 */
	void insertBatch(int dsId, String className, List<String> dataList) throws SQLException;

}
