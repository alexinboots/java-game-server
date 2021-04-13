package net.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件工具类
 *
 * @author chao
 */
public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 创建指定文件的目录
	 * 
	 * @param file
	 * @return
	 */
	public static boolean createMissingParentDirectories(File file) {
		File parent = file.getParentFile();
		if (parent == null) {
			// Parent directory not specified, therefore it's a request to
			// create nothing. Done! ;)
			return true;
		}

		// File.mkdirs() creates the parent directories only if they don't
		// already exist; and it's okay if they do.
		parent.mkdirs();
		return parent.exists();
	}

	/**
	 * 文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean existFile(String fileName) {
		File file = new File(fileName);
		return (file.exists()) && (file.isFile());
	}

	/**
	 * read a file to a string
	 */
	public static String readFile(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(br);
		}

		return null;
	}

	/**
	 * read a file to a string
	 */
	public static String readFile(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(br);
		}

		return null;
	}

	/**
	 * get md5 of file
	 */
	public static String fileMD5(String filePath) {
		FileInputStream fis = null;
		String md5 = null;
		try {
			fis = new FileInputStream(new File(filePath));
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(fis);
		}
		return md5;
	}

	/**
	 * get md5 of file
	 */
	public static String fileMD5(File file) {
		FileInputStream fis = null;
		String md5 = null;
		try {
			fis = new FileInputStream(file);
			md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(fis);
		}
		return md5;
	}

	/**
	 * Function name:saveChatTxt Description: 将字符串写入文件
	 * 
	 * @param text
	 *            :
	 * @param path
	 *            :
	 * @return 写入成功返回true，否则返回false
	 */
	public static boolean saveChatTxt(String text, String path) {
		if (text == null) {
			return false;
		}
		boolean ok = false;
		FileOutputStream to = null;
		PrintWriter out = null;
		try {
			File fo = new File(path);

			// 文件存在,先删除
			if (fo.exists()) {
				fo.delete();
			}

			// 创建新文件
			fo.createNewFile();

			// 创建文件输出流
			to = new FileOutputStream(fo, false);
			// 输出流
			out = new PrintWriter(to);
			out.append(text);
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				out.close(); // 关闭流
				to.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ok;
	}

	public static boolean saveChatTxt(List<String> chats, String path) {
		return saveChatTxt(chats, path, false);
	}

	/**
	 * Function name:saveChatTxt Description: 将字符集合写入文件
	 * 
	 * @param chats
	 *            :
	 * @param path
	 *            :
	 * @return
	 */
	public static boolean saveChatTxt(List<String> chats, String path, boolean append) {
		if (chats == null || chats.size() < 1) {
			return true;
		}
		boolean ok = false;
		File fo = null;
		FileOutputStream to = null;
		PrintWriter out = null;
		try {
			fo = new File(path);
			// 文件存在且不追加,先删除
			if (fo.exists() && !append) {
				fo.delete();
			}

			// 如果文件不存在
			if (!fo.exists()) {
				fo.createNewFile();
			}

			to = new FileOutputStream(fo, append); // 创建文件输出流
			out = new PrintWriter(to); // 输出流

			for (String msg : chats) {
				out.println(msg);
			}
			// out.println("------------------------导出成功,共:" + chats.size() +
			// "条记录.");
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			CloseUtils.close(out);
			CloseUtils.close(to);
		}
		return ok;
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + File.separator + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + File.separator + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 把文件按行读入并返回list
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> readFileReturnList(String filePath) {
		List<String> rs = new ArrayList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line = br.readLine();

			while (line != null) {
				rs.add(line);
				line = br.readLine();
			}
		} catch (Exception e) {
			logger.error("!!!", e);
		} finally {
			CloseUtils.close(br);
		}

		return rs;
	}

	/**
	 * @param path
	 * @param data
	 * @param append
	 *            :是否追加到文件末尾
	 * @return
	 */
	public static boolean saveBytes(String path, byte[] data, boolean append) {
		File ff = new File(path);
		if (ff.exists()) {
			System.out.println("------------警告,覆盖文件:" + path);
		}
		boolean r = false;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path, append);
			out.write(data);
			r = true;
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return r;
	}

	/**
	 * 加载文件,返回字节数组
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] loadBytes(String path) {
		byte[] msgData = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			if (in != null && in.available() > 0) {
				msgData = new byte[in.available()];
				in.read(msgData);
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return msgData;
	}

	public static void bytes2File(byte[] bytes, String filePath) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = new File(filePath);
		try {
			String parentPath = file.getParent();
			File dir = new File(parentPath);
			if (!dir.exists()) {// 判断文件目录是否存在
				dir.mkdirs();
			}

			file.createNewFile();

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static long getFileSize(String fileName) {
		try {
			RandomAccessFile rafFile = new RandomAccessFile(fileName, "rb");
			long size = rafFile.length();
			rafFile.close();
			return size;
		} catch (Exception e) {
			Tool.catchException(e);
		}
		return 0L;
	}

	public static boolean osDeleteFile(String fileName) {
		File file = new File(fileName);
		return file.delete();
	}

	public static boolean renameFile(String fileName, String newFileName) {
		File file = new File(fileName);
		return file.renameTo(new File(newFileName));
	}

}
