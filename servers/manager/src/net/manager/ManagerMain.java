package net.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.GenericApplicationContext;
import com.alibaba.nacos.api.exception.NacosException;
import net.server.ServerInfo;
import net.utils.AppUtils;
import net.utils.NacosConfigUtils;

/**
 * @Author ckf
 */
public class ManagerMain {
	private static Logger logger = LoggerFactory.getLogger(ManagerMain.class);

	public static GenericApplicationContext context;
	public static ServerInfo serverInfo;

	@ComponentScan(basePackages = "net.manager") @EnableAspectJAutoProxy public static class BeanConfigure {

		@Autowired void init() throws SQLException {

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
		logger = LoggerFactory.getLogger(ManagerMain.class);

		String filePath = AppUtils.getConfigFile("managernacos.properties");
		try {
			serverInfo = NacosConfigUtils.getServerInfo(filePath);
		} catch (NacosException e) {
			logger.error("服务器启动异常，从nacos配置中心获取数据失败e = ", e);
		}
	}

	private static void serverStart(String[] args) {
		context = new AnnotationConfigApplicationContext(BeanConfigure.class);

		Runtime.getRuntime().addShutdownHook(new Thread() {//程序正常关闭时一定执行的代码
			@Override public void run() {
				//调用System.exit(0)时才会触发a
				serverStop();
			}
		});

		logger.info("中心服启动完成a");
	}

	/**
	 * 停服
	 */
	private static void serverStop() {

		CloseableUtils.closeQuietly(context);

		logger.info("");
	}

	/**
	 * 后台指令
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
					//Server stop!
					System.out.println("...");
					break;
				}
			} catch (Exception e) {
				System.out.println("执行命令失败:" + e.getMessage());
				e.printStackTrace();
			}
		}

		System.exit(0);
	}
}

