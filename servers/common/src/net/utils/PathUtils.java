package net.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * 路径常用操作封装
 * 
 * @author bliang
 * @date 2019/10/17
 */
public class PathUtils {

	public static String PATH_SEPARATOR = File.separator;

	/**
	 * 取出目录路径
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getFileName(String fullName) {
		Objects.requireNonNull(fullName);

		String fileName = new File(fullName).getName();
		int idx = fileName.lastIndexOf(PATH_SEPARATOR);

		//如果没有名字，则会抛出异常
		return (idx == -1) ? fileName : fileName.substring(idx + 1);
	}

	/**
	 * 取出没有扩展名的文件名
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getNameWithoutExtension(String fullName) {
		String fileName = getFileName(fullName);

		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
	}

	/**
	 * 取出文件扩展名部分
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getFileExtension(String fullName) {
		Objects.requireNonNull(fullName);

		String fileName = new File(fullName).getName();
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == fileName.length() - 1) {
			return "";
		}

		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

	/**
	 * 取出目标路径所在的父目录， 目标路径可以是目录也可以是文件
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getDirectoryName(String path) throws IOException {
		Objects.requireNonNull(path);

		//先拿到绝对路径
		File file = new File(path);
		String fileName = file.getCanonicalPath();

		file = new File(fileName);

		return file.getParent();
	}

	/**
	 * Given a parent path and a child node, create a combined full path
	 *
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 * @return full path
	 */
	public static String combine(String parent, String child) {
		StringBuilder path = new StringBuilder();

		path.append(parent);
		combine(path, child);

		return path.toString();
	}

	/**
	 * Given a parent path and a list of children nodes, create a combined full
	 * path
	 *
	 * @param parent
	 *            the parent
	 * @param firstChild
	 *            the first children in the path
	 * @param secondChild
	 *            the second children in the path
	 * @param restChildren
	 *            the rest of the children in the path
	 * @return full path
	 */
	public static String combine(String parent, String firstChild, String secondChild, String... restChildren) {
		StringBuilder path = new StringBuilder();

		path.append(parent);
		combine(path, firstChild);
		combine(path, secondChild);

		if (restChildren == null) {
			return path.toString();
		} else {
			for (String child : restChildren) {
				combine(path, child);
			}
			return path.toString();
		}
	}

	/**
	 * 追加路径
	 * {@link StringBuilder path}
	 *
	 * @param path
	 *            the {@link StringBuilder} used to make the path
	 * @param child
	 *            the child
	 */
	private static void combine(StringBuilder path, String child) {
		if (child == null || child.length() == 0) {
			return;
		}

		if (path.length() == 0) {
			path.append(child);
			return;
		}

		int start = 0;
		int end = child.length();
		if (child.startsWith(PATH_SEPARATOR)) {
			start = PATH_SEPARATOR.length();
		}

		if (child.endsWith(PATH_SEPARATOR)) {
			end = child.length() - PATH_SEPARATOR.length();
		}

		if (path.lastIndexOf(PATH_SEPARATOR) != path.length() - PATH_SEPARATOR.length()) {
			path.append(PATH_SEPARATOR);
		}
		path.append(child.substring(start, end));
	}

	/**
	 * 找到系统javahome路径
	 * 
	 * @return
	 */
	public static String findJavaHome() {
		String javaHome = System.getProperty("java.home");
		File toolsJar = new File(javaHome, "lib/tools.jar");
		if (!toolsJar.exists()) {
			toolsJar = new File(javaHome, "../lib/tools.jar");
		}

		if (!toolsJar.exists()) {
			toolsJar = new File(javaHome, "../../lib/tools.jar");
		}

		if (toolsJar.exists()) {
			return javaHome;
		}

		if (!JavaVersionUtils.isLessThanJava9()) {
			return null;
		}

		if (!toolsJar.exists()) {
			String javaHomeEnv = System.getenv("JAVA_HOME");
			if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
				toolsJar = new File(javaHomeEnv, "lib/tools.jar");
				if (!toolsJar.exists()) {
					toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
				}
			}

			if (toolsJar.exists()) {
				return javaHomeEnv;
			}
		}

		return null;
	}

	public static String getClassNameWithPackage(String className) {
		ArrayList<String> filter = new ArrayList<>();
		filter.add(className);
		Set<Class> clazzs = ClassPathScanner.scan("net.good321", true, false, true, filter);
		for (Class clazz : clazzs) {
			return clazz.getName();
		}

		return null;
	}
}
