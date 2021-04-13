package net.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 简单的文件目录操作封装
 * 
 * @author chao
 * 
 */
public class DirUtils {

	private static final Logger logger = LoggerFactory.getLogger(DirUtils.class);

	private static String PATH_SEPARATOR = File.separator;

	/**
	 * 返回一个目录的文件，args只接受正则表达式
	 */
	public static File[] ls(String path, String... args) {
		File dir = new File(path);

		if (args.length == 0) {
			return dir.listFiles();
		}

		final Pattern[] patterns = new Pattern[args.length];
		for (int i = 0; i < args.length; i++) {
			patterns[i] = Pattern.compile(args[i]);
		}

		return dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				for (Pattern pattern : patterns) {
					Matcher matcher = pattern.matcher(name);
					if (matcher.find()) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * 递归子目录返回所有符合的文件，args只接受正则表达式
	 */
	public static File[] recursive(String path, String... args) {
		List<File> resLs = new ArrayList<File>();
		Queue<File> dirs = new LinkedList<File>();

		File file = new File(path);
		dirs.offer(file);

		final Pattern[] patterns = new Pattern[args.length];
		for (int i = 0; i < args.length; i++) {
			patterns[i] = Pattern.compile(args[i]);
		}

		while (dirs.size() != 0) {
			File tempPath = dirs.poll();
			File[] files = tempPath.listFiles();
			// 遍历files,如果是目录继续搜索,文件则匹配正则表达式
			for (File tFile : files) {
				if (tFile.isDirectory()) {
					dirs.offer(tFile);
				} else {
					for (Pattern pattern : patterns) {
						Matcher matcher = pattern.matcher(tFile.getName());
						if (matcher.find()) {
							resLs.add(tFile);
							break;
						}
					}
				}
			}
		}

		File[] res = new File[resLs.size()];
		resLs.toArray(res);
		return res;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param to
	 *            必须是目录
	 */
	public static boolean cp(String from, String to, String... args) {
		// buffer为2M
		int length = 2097152;
		File fromFile = new File(from);
		File toFile = new File(to);
		// 目录的话相当于cp -r * 正则表达式对每一层都有效
		if (fromFile.isDirectory()) {
			File[] childFiles = ls(from, args);
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					mkdir(to + File.separator + childFile.getName());
				}
				cp(childFile.getPath(), to + File.separator + childFile.getName(), args);
			}
			return true;
		} else {
			FileInputStream in = null;
			FileOutputStream out = null;
			try {
				in = new FileInputStream(from);
				String toPath = to;
				if (toFile.isDirectory()) {
					toPath += File.separator + fromFile.getName();
				}
				out = new FileOutputStream(toPath);
				byte[] buffer = new byte[length];
				while (true) {
					int ins = in.read(buffer);
					if (ins == -1) {
						in.close();
						out.flush();
						out.close();
						return true;
					} else
						out.write(buffer, 0, ins);
				}
			} catch (Exception e) {
				logger.error("cp error", e);
				return false;
			} finally {
				CloseUtils.close(in);
				CloseUtils.close(out);
			}
		}
	}

	/**
	 * 删除单个文件
	 */
	public static void rm(String path, String... args) {
		File file = new File(path);

		// 目录的话相当于 rm -r path/* 不会删除文件夹 正则表达式只对一层有效
		if (file.isDirectory()) {
			File[] childFiles = ls(path, args);
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					rm(childFile.getPath());
				}

				if (!childFile.delete()) {
					logger.error("file delete failed. filename=" + childFile.getAbsolutePath());
				}
			}
		} else {
			if (!file.delete()) {
				logger.error("file delete failed. filename=" + file.getAbsolutePath());
			}
		}
	}

	/**
	 * 创建文件夹
	 */
	public static boolean mkdir(String path) {
		File file = new File(path);
		return file.mkdirs();
	}

	/**
	 * 删除空文件夹
	 */
	public static boolean rmdir(String path) {
		File file = new File(path);
		return file.delete();
	}

	/**
	 * 文件夹是否存在
	 * 
	 * @param folderName
	 * @return
	 */
	public static boolean existDir(String folderName) {
		File file = new File(folderName);
		return (file.exists()) && (file.isDirectory());
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
	@Deprecated
	public static String makePath(String parent, String child) {
		StringBuilder path = new StringBuilder();

		joinPath(path, parent, child);

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
	 * @param restChildren
	 *            the rest of the children in the path
	 * @return full path
	 */
	@Deprecated
	public static String makePath(String parent, String firstChild, String... restChildren) {
		StringBuilder path = new StringBuilder();

		joinPath(path, parent, firstChild);

		if (restChildren == null) {
			return path.toString();
		} else {
			for (String child : restChildren) {
				joinPath(path, "", child);
			}

			return path.toString();
		}
	}

	/**
	 * Given a parent and a child node, join them in the given
	 * {@link StringBuilder path}
	 *
	 * @param path
	 *            the {@link StringBuilder} used to make the path
	 * @param parent
	 *            the parent
	 * @param child
	 *            the child
	 */
	@Deprecated
	private static void joinPath(StringBuilder path, String parent, String child) {
		// Add parent piece, with no trailing slash.
		if ((parent != null) && (parent.length() > 0)) {
			if (!parent.startsWith(PATH_SEPARATOR)) {
				path.append(PATH_SEPARATOR);
			}
			if (parent.endsWith(PATH_SEPARATOR)) {
				path.append(parent.substring(0, parent.length() - 1));
			} else {
				path.append(parent);
			}
		}

		if ((child == null) || (child.length() == 0) || (child.equals(PATH_SEPARATOR))) {
			// Special case, empty parent and child
			if (path.length() == 0) {
				path.append(PATH_SEPARATOR);
			}
			return;
		}

		// Now add the separator between parent and child.
		path.append(PATH_SEPARATOR);

		if (child.startsWith(PATH_SEPARATOR)) {
			child = child.substring(1);
		}

		if (child.endsWith(PATH_SEPARATOR)) {
			child = child.substring(0, child.length() - 1);
		}

		// Finally, add the child.
		path.append(child);
	}
}
