package net.db.sqlhandler;

/**
 * 查询数据结果处理器
 * 
 * @Author ckf
 */
public enum SqlHandlerType {
	
	/**
	 * list
	 */
	LIST(1, "listResultHanlder"),

	;
	
	SqlHandlerType(int value, String name) {
		this.value = value;
		this.name = name;
	}

	private int value;

	private String name;

	public static SqlHandlerType valueOf(int type) {
		for (SqlHandlerType sqlHandlerType : SqlHandlerType.values()) {
			if (sqlHandlerType.getValue() == type) {
				return sqlHandlerType;
			}
		}

		return null;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return name;
	}
}
