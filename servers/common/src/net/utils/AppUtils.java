package net.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 工具类
 * 
 * @author bliang
 * @date 2020/07/13
 */
public class AppUtils {

	/**
	 * 等待指定的服务
	 * 
	 * @param predicate
	 * @param printTips
	 */
	public static <T> T waitingService(String serviceName, Supplier<T> supplier) {
		Objects.requireNonNull(supplier);

		while (true) {
			T obj = supplier.get();
			if (obj != null) {
				return obj;
			} else {
				System.out.println("waiting " + serviceName + "...");
				Tool.sleep(1000);
			}
		}
	}

	/**
	 * 加载外部logback.xml文件
	 * 
	 * @param externalXmlConfigFile
	 * @throws IOException
	 * @throws JoranException
	 */
	public static void loadLogConfig(String externalXmlConfigFile) throws IOException, JoranException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		File externalConfigFile = new File(externalXmlConfigFile);

		if (!externalConfigFile.exists()) {
			throw new IOException("Logback External Config File Parameter does not reference a file that exists");
		}

		if (!externalConfigFile.isFile()) {
			throw new IOException("Logback External Config File Parameter exists, but does not reference a file");
		}

		if (!externalConfigFile.canRead()) {
			throw new IOException("Logback External Config File exists and is a file, but cannot be read.");
		}

		//只能读取外部logback.xml文件
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		configurator.doConfigure(externalXmlConfigFile);

		StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
	}

	/**
	 * 初始化环境变量
	 */
	public static void initEnvSetting() {
		String homeDir = System.getProperty("wwz.home");

		//当前启动项目的路径
		String projectHome = getProjectHome();
		System.setProperty("project.home", projectHome);

		//当前启动项目的文件夹名字
		String projectName = getProjectName();

		///////////////////////////////////////////////////////////////////////////////////////
		//配置文件默认路径
		String confPath = projectHome;
		if (homeDir != null) {
			confPath = PathUtils.combine(homeDir, "base_conf", projectName);
		} else {
			confPath = PathUtils.combine(projectHome, "conf");
		}

		System.setProperty("conf.path", confPath);

		///////////////////////////////////////////////////////////////////////////////////////
		//资源和数据默认路径
		String dataPath = projectHome;
		if (homeDir != null) {
			dataPath = PathUtils.combine(homeDir, "base_data", projectName);
		}
		System.setProperty("data.path", dataPath);

		///////////////////////////////////////////////////////////////////////////////////////
		//日志目录默认为项目路径
		String logHome = projectHome;
		String logPath = System.getProperty("logpath");
		//如果指定了日志根目录，则使用指定路径
		if (logPath != null) {
			logHome = PathUtils.combine(logPath, projectName);
		}

		System.setProperty("log.home", logHome);
		///////////////////////////////////////////////////////////////////////////////////////

		//游族日志路径变量
		if (SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("yzlog.home", PathUtils.combine(logHome, "/log/yz"));
		} else {
			System.setProperty("yzlog.home", "/home/datacenter/td_data");
		}
		/////////////////////////////////////////////////////////////////////////////////////

		//配置文件路径
		String logbackCfg = getConfigFile("logback.groovy");
		System.setProperty("logback.configurationFile", logbackCfg);
		/////////////////////////////////////////////////////////////////////////////////////

		System.out.println("project.home=" + projectHome);
		System.out.println("log.home=" + logHome);
	}

	/**
	 * 获取当前项目路径
	 * 
	 * @return
	 */
	public static String getStartupPath() {
		//获取执行此方法时，PathUtils所在的路径
		//String filePath = PathUtils.class.getResource(".").getPath();
		//String filePath = ClassLoader.getSystemResource(".").getPath();
		//File file = new File(filePath);
		//String projectHome = file.getAbsolutePath();

		String projectHome = System.getProperty("user.dir");
		return projectHome;
	}

	/**
	 * 获取当前项目名字
	 * 
	 * @return 返回项目名
	 */
	public static String getProjectName() {
		//取出当前项目路径
		String projectHome = getStartupPath();

		//截取项目名字
		int idx = projectHome.lastIndexOf(PathUtils.PATH_SEPARATOR);
		if (idx == -1) {
			return projectHome;
		} else {
			String parentDir = projectHome.substring(idx + PathUtils.PATH_SEPARATOR.length());
			idx = parentDir.indexOf('_');
			if (idx > 0) {
				parentDir = parentDir.substring(0, idx);
			}

			return parentDir;
		}
	}

	/**
	 * 获取wwz的当前数据目录
	 * 
	 * @return
	 */
	public static String getWWZHome() {
		String wwzHome = System.getProperty("wwz.home");

		//如果没有指定的配置路径，则使用当前项目目录
		if (StringUtils.isEmpty(wwzHome)) {
			return null;
		}

		try {
			File file = new File(wwzHome);

			//如果不是绝对路径，则转为绝对路径
			if (!file.isAbsolute()) {
				wwzHome = file.getCanonicalPath();
			}
		} catch (IOException e) {
			wwzHome = System.getProperty("user.dir");

			//此处不用logback，因为logback本身要调用此方法
			e.printStackTrace();
		}

		return wwzHome;
	}

	/**
	 * 获取当前启动项目的数据根目录，如果配置指定的路径，则使用指定路径
	 * 
	 * @return 当前项目的数据根目录
	 */
	public static String getProjectHome() {
		String projectHome = System.getProperty("project.home");

		// 定义了变量，则直接返回，优先级最高
		if (projectHome != null) {
			return projectHome;
		}

		String homeDir = getWWZHome();

		//如果指定了数据目录，则路径为home/projectName
		if (homeDir != null) {
			String projectName = getProjectName();
			projectHome = PathUtils.combine(homeDir, projectName);
		} else {
			//否则就是当前启动路径
			projectHome = getStartupPath();
		}

		//删除最后的路径符
		if (projectHome.endsWith(PathUtils.PATH_SEPARATOR)) {
			projectHome = projectHome.substring(0, projectHome.length() - PathUtils.PATH_SEPARATOR.length());
		}

		return projectHome;
	}

	/**
	 * 获取配置文件路径
	 * 
	 * @return
	 */
	public static String getConfPath() {
		String confPath = System.getProperty("conf.path");
		return confPath;
	}

	/**
	 * 获取数据文件路径
	 * 
	 * @return
	 */
	public static String getDataPath() {
		String dataPath = System.getProperty("data.path");
		return dataPath;
	}

	/**
	 * 获取日志文件路径
	 * 
	 * @return
	 */
	public static String getLogHome() {
		String dataPath = System.getProperty("log.home");
		return dataPath;
	}

	/**
	 * 保存状态文件,本地记录一个服务器状态,方便运维脚本处理
	 * 
	 * @param status
	 */
	public static void saveStatusTxt(String status) {
		String logHome = getLogHome();
		String statusFile = PathUtils.combine(logHome, "status.txt");
		if (!DirUtils.existDir(logHome)) {
			DirUtils.mkdir(logHome);
		}
		FileUtils.saveChatTxt(ImmutableList.of(status), statusFile);
	}

	/**
	 * 返回配置文件的绝对路径
	 * 
	 * @param subPath
	 * @return
	 */
	public static String getConfigFile(String fileName) {
		String confPath = getConfPath();
		String filePath = PathUtils.combine(confPath, fileName);
		return filePath;
	}

	/**
	 * 先从启动参数获取id，如果没有再从配置表获取serverId
	 * 
	 * @param configFile
	 * @param args
	 * @return
	 */
	public static int getServerId(String fileName, String key, String[] startupArgs) {
		int serverId = 0;

		try {
			//如果配置了启动参数，则先用启动参数
			if (startupArgs != null && startupArgs.length > 0) {
				serverId = Integer.parseInt(startupArgs[0]);
				System.out.println("init serverId from startup params,serverId=" + serverId);
			} else {
				String filePath = getConfigFile(fileName);
				Properties properties = new Properties();
				properties.load(new FileInputStream(new File(filePath)));
				String tServerId = properties.getProperty(key);

				Objects.requireNonNull(tServerId,
						"serverId is null,it must set in configuration file or startup parameters!");
				serverId = Integer.parseInt(tServerId);

				System.out.println("init serverId from " + fileName + ",serverId=" + serverId);
			}

			System.setProperty("serverId", serverId + "");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return serverId;
	}
}
