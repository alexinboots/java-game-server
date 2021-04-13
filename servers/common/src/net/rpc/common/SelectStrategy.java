package net.rpc.common;

/**
 * 选择策略
 * 
 * @Author ckf
 */
public interface SelectStrategy {

	/**
	 * 根据具体策略取得服务器id
	 * 
	 * @return 
	 */
	int selectServerId();
}
