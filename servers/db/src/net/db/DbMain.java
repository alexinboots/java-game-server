package net.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.alibaba.nacos.api.exception.NacosException;
import net.db.dao.AsyncData;
import net.db.dao.RedisCache;
import net.db.manager.DataManager;
import net.server.DbServerInfo;
import net.utils.AppUtils;
import net.utils.NacosConfigUtils;
import net.utils.SpringContext;
import net.utils.Tool;

/**
 * DbMain
 *
 * @author chao
 * @version 1.0 - 2014-05-30
 */

public class DbMain {

	private static Logger logger = null;

	public static ApplicationContext context;

	public static DbServerInfo serverInfo;

	@ComponentScan(basePackages = "net.db")
	public static class BeanConfigure {
		
		@Bean SpringContext springContext() {
			return new SpringContext();
		}
	}

	public static void main(String[] args) {
		initServer(args);

		serverStart(args);

		waitCommand(args);
	}

	/**
	 * 初始化服务器
	 *
	 * @param args
	 */
	private static void initServer(String[] args) {
		AppUtils.initEnvSetting();

		logger = LoggerFactory.getLogger(DbMain.class);

		String filePath = AppUtils.getConfigFile("nacos.properties");
		try {
			serverInfo = NacosConfigUtils.getDbServerInfo(filePath);
		} catch (NacosException e) {
			logger.error("服务器启动异常，从nacos配置中心获取数据失败e = ", e);
		}
	}

	/**
	 * 开服
	 */
	private static void serverStart(String[] args) {
		context = new AnnotationConfigApplicationContext(BeanConfigure.class);

		//测试redis连接
		DataManager dataManager = context.getBean(DataManager.class);
		for (RedisCache redisCache : dataManager.getRedisCaches().values()) {
			boolean isRunning = redisCache.check();
			if (!isRunning) {
				logger.error("redis {}:{} 连接不上！！！！！！！！", redisCache.getHost(), redisCache.getPort());
				throw new RuntimeException("redis 连接不上");

			} else {
				logger.info("redis {}:{} 连接成功", redisCache.getHost(), redisCache.getPort());
			}

		}

		//程序关闭时一定执行的代码
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override public void run() {
				//调用System.exit(0)时才会触发
				serverStop();
			}
		});

		//本地记录一个服务器状态,方便运维脚本处理
		AppUtils.saveStatusTxt("on");

		System.out.println("服务器已经启动:" + System.currentTimeMillis());
	}

	/**
	 * 停服
	 */
	private static void serverStop() {
		System.err.println("数据服务器准备关闭...");

		AsyncData data = context.getBean(AsyncData.class);
		data.shutdown();
		
		//等待5秒，等其他线程的结束流程处理完毕
		int waitTime = 5000;
		System.out.println("距离关闭还有" + TimeUnit.MILLISECONDS.toSeconds(waitTime) + "秒");
		Tool.sleep(waitTime);

		//本地记录一个服务器状态,方便运维脚本处理
		AppUtils.saveStatusTxt("off");

		System.out.println("服务器已经安全关闭:" + System.currentTimeMillis());
	}

	/**
	 * 接受指令
	 *
	 * @param args
	 */
	private static void waitCommand(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("输入命令:<stop/test/other...>");
			try {
				String str = br.readLine().trim();
				if (str.equals("stop")) {
					break;
				} else {
					doSomeThing(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("执行命令失败:遇到致命错误");
			}
		}

		System.exit(0);
	}

	/**
	 * 后台指令
	 *
	 * @param str
	 */
	private static void doSomeThing(String str) {
		if (str.equals("test")) {
			System.out.println("服务器正在运行,当前时间:" + System.currentTimeMillis());
		} else {
			System.out.println("不接受此命令: " + str);
		}
	}

}
