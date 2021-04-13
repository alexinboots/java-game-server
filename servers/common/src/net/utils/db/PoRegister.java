package net.utils.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.db.BasePo;
import net.db.PoProxy;
import net.utils.CloseUtils;

/**
 * po注册
 *
 * @author chao
 */
public class PoRegister {
	private static Logger logger = LoggerFactory.getLogger(PoRegister.class);

	public static Map<Class, PoProxy> scan(Set<Class> classes, DataSource ds) {
		Map<Class, PoProxy> PoProxys = new HashMap<>();

		for (Class cls : classes) {
			PoProxy table = new PoProxy((Class<? extends BasePo>) cls, ds);
			PoProxys.put(cls, table);
		}

		checkTableColumns(PoProxys);

		return PoProxys;
	}

	/**
	 * 检查数据表的列是否与po的字段一致
	 * 
	 * @param tableObjs
	 * @return
	 */
	private static boolean checkTableColumns(Map<Class, PoProxy> tableObjs) {
		boolean hasErr = false;

		for (PoProxy table : tableObjs.values()) {
			Connection conn = null;
			ResultSet rst = null;
			try {
				conn = table.ds.getConnection();
				rst = conn.getMetaData().getColumns(null, null, table.tbName, null);
				Map<String, String> columnNames = new HashMap<String, String>();
				while (rst.next()) {
					String cname = rst.getString("COLUMN_NAME");
					columnNames.put(cname, cname);
				}

				BasePo ins = (BasePo) table.cls.newInstance();

				for (String columnName : ins.props()) {
					columnName = columnName.replace("`", "");
					if (columnNames.get(columnName) == null) {
						hasErr = true;
						logger.error("***********数据表缺少字段,tableName=: " + table.tbName + ",columnName = " + columnName);
					}
				}

			} catch (Exception e) {
				logger.error("***********数据表字段检查出现异常,tableName=: " + table.tbName, e);
			} finally {
				CloseUtils.close(rst);
				CloseUtils.close(conn);
			}
		}

		if (hasErr) {
			throw new RuntimeException("启动失败，数据表字段检查不通过！");
		}

		return hasErr;
	}
}
