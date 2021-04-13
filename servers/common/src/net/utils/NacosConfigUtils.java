package net.utils;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.typesafe.config.ConfigException;
import net.server.DataServerInfo;
import net.server.DbServerInfo;
import net.server.ServerInfo;

/**
 * nacos配置服务工具
 *
 * @Author ckf
 */
public class NacosConfigUtils {

	private static Logger logger = LoggerFactory.getLogger(NacosConfigUtils.class);

	/**
	 * 根据配置初始化并拿到本服配置
	 *
	 * @param confPath 基础配置文件路径
	 * @throws NacosException
	 */
	public static String getConf(String confPath) throws NacosException {
		File filePath = new File(confPath);
		if (filePath.exists() && filePath.isFile()) {
			logger.info("use external nacos config file. file={}", filePath.getAbsoluteFile());

		} else {
			URL fileUrl = NacosConfigUtils.class.getClassLoader().getResource(confPath);

			//依然读取失败，则抛出异常
			if (fileUrl == null) {
				throw new ConfigException("读取nacos的配置文件失败，file=" + confPath) {
				};
			}

			filePath = new File(fileUrl.getPath());
		}

		Properties confProp = PropertiesUtils.load(filePath);
		String nacosAddr = confProp.getProperty("serverAddr");
		String group = confProp.getProperty("group");
		String dataId = confProp.getProperty("dataId");
		int nacosTimeOut = Integer.parseInt(confProp.getProperty("timeout"));
		Properties properties = new Properties();
		properties.put("serverAddr", nacosAddr);
		ConfigService configService = NacosFactory.createConfigService(properties);
		return configService.getConfig(dataId, group, nacosTimeOut);
	}
	
	public static ServerInfo getServerInfo(String confPath) throws NacosException {
		String conf = getConf(confPath);
		return JsonUtils.string2Object(conf, ServerInfo.class);
	}
	
	public static DbServerInfo getDbServerInfo(String confPath) throws NacosException {
		String conf = getConf(confPath);
		return JsonUtils.string2Object(conf, DbServerInfo.class);
	}

	public static DataServerInfo getDataServerInfo(String confPath) throws NacosException {
		String conf = getConf(confPath);
		return JsonUtils.string2Object(conf, DataServerInfo.class);
	}
}