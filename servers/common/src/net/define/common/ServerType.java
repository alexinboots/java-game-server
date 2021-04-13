package net.define.common;

/**
 * @Author ckf
 */
public enum ServerType {
	
	/**
	 * 管理服
	 */
	MANAGER(1, "mananger"),

	/**
	 * 游戏逻辑服
	 */
	GAME(2, "game"),

	/**
	 * 数据服
	 */
	DB(3, "db"),
	;
	
	ServerType(int value, String name) {
		this.value = value;
		this.name = name;
	}

	private int value;

	private String name;

	public static ServerType valueOf(int type) {
		for (ServerType serverType : ServerType.values()) {
			if (serverType.getValue() == type) {
				return serverType;
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
