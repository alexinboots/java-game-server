package net.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @createTime 2017年11月22日 上午11:30:59
 */
public class PropertiesUtils {

	public static Properties load(String fileName) {

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		return load(is);
	}

	public static Properties load(File file) {
		try {
			InputStream is = new FileInputStream(file);
			return load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Properties load(InputStream is) {

		Properties properties = new Properties();
		try {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
		return properties;
	}

}
