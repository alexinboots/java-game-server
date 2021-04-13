package net.utils;

import java.io.Closeable;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可关闭对象关闭工具
 * 
 * @author chao
 *
 */
public class CloseUtils {
	private static final Logger logger = LoggerFactory.getLogger(CloseUtils.class);

	/** 关闭 */
	public static void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		
		try {
			closeable.close();
		} catch (IOException e) {
			logger.error("closer exception!", e);
		}
	}

	/** 关闭 */
	public static void close(AutoCloseable autoCloseable) {
		if (autoCloseable == null) {
			return;
		}

		try {
			autoCloseable.close();
		} catch (Exception e) {
			logger.error("closer exception!", e);
		}
	}
}
