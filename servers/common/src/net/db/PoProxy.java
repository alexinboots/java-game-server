package net.db;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.db.anno.PO;
import net.utils.db.SqlUtils;

/**
 * 数据库表操作代理
 * 
 * @Author ckf
 */
public class PoProxy {
	
	private static Logger logger = LoggerFactory.getLogger(PoProxy.class);

	public final Class<?> cls;
	public final DataSource ds;
	public final String tbName;
	public final String selectAll;
	public final String select;
	public final String deleteAll;
	public final String delete;
	public final String update;
	public final String insert;

	public PoProxy(Class<? extends BasePo> cls, DataSource ds) {
		this.cls = cls;
		this.ds = ds;
		PO po = cls.getAnnotation(PO.class);
		this.tbName = po.value();

		BasePo ins = null;
		try {
			ins = cls.newInstance();
		} catch (Exception e) {
			logger.error("!!!", e);
		}

		this.selectAll = SqlUtils.selectAll(tbName);
		this.deleteAll = SqlUtils.deleteAll(tbName);
		
		this.select = SqlUtils.select(tbName, ins.ids());
		this.delete = SqlUtils.delete(tbName, ins.ids());
		this.update = SqlUtils.update(tbName, ins.props(), ins.ids());
		this.insert = SqlUtils.insert(tbName, ins.props());
	}

}
