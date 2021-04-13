package net.db;

import java.io.Serializable;

/**
 * 表数据抽象类
 * 
 * @author ckf
 */
public abstract class BasePo implements Serializable {

	/**
	 * id的列，可用于调整顺序分表
	 * @return
	 */
	abstract public String[] ids();

	/**
	 * 所有属性列
	 * @return
	 */
	abstract public String[] props();

	/**
	 * 所有属性列的值
	 * @return
	 */
	abstract public Object[] propValues();

	/**
	 * 所有id列的值
	 * @return
	 */
	abstract public Object[] idValues();
}
