package net.db.sqlhandler;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 * 列表结果处理器
 * 
 * @author ckf
 */
public class ListResultSetHandler<T> implements ResultSetHandler<List<T>>, Serializable {

	private static final long serialVersionUID = -628124827267281583L;

	private Class<T> clazz;
	private boolean isComplexType;
	private transient BeanListHandler<T> beanListHandler;

	public ListResultSetHandler(Class<T> paramType) {
		this.clazz = paramType;
		this.isComplexType = isComplexClass(paramType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> handle(ResultSet rs) throws SQLException {
		try {
			List<T> dataList = new ArrayList<>();

			if (isComplexType) {
				beanListHandler = new BeanListHandler<>(clazz);
				dataList = beanListHandler.handle(rs);
			} else {
				while (rs.next()) {
					T data = (T) rs.getObject(1);
					dataList.add(data);
				}
			}
			return dataList;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 是否复杂类型
	 * 
	 * @param type
	 * @return
	 */
	private boolean isComplexClass(Class<?> type) {
		// Do object check first, then primitives
		if (type.equals(Integer.class)) {
			return false;

		} else if (type.equals(Long.class)) {
			return false;

		} else if (type.equals(Double.class)) {
			return false;

		} else if (type.equals(Float.class)) {
			return false;

		} else if (type.equals(Short.class)) {
			return false;

		} else if (type.equals(Byte.class)) {
			return false;

		} else if (type.equals(Character.class)) {
			return false;

		} else if (type.equals(Boolean.class)) {
			return false;

		} else if (type.equals(String.class)) {
			return false;
		}
		return true;
	}
}
