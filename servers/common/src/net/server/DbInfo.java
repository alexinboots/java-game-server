package net.server;

/**
 * 数据库信息
 * 
 * @Author ckf
 */
public class DbInfo {

	/**
	 * 数据库id
	 */
	private int dbId;
	
	/**
	 * 数据库名
	 */
	private String name;

	/**
	 * 数据库url
	 */
	private String url;

	/**
	 * 数据库账号
	 */
	private String username;

	/**
	 * 数据库密码
	 */
	private String password;

	/**
	 * 初始大小
	 */
	private int initilSize;
	private int minIdle;
	private int maxActive;
	private String validationQuery;
	private boolean testOnBorrow;
	private int minEvictableIdleTimeMillis;
	private int timeBetweenEvictionRunsMillis;

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getInitilSize() {
		return initilSize;
	}

	public void setInitilSize(int initilSize) {
		this.initilSize = initilSize;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public int getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public int getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}
}
