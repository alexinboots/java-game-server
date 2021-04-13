package net.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * string工具
 * 
 * @author chao
 *
 */
public class StringUtils {

	//分割符
	public final static String regex = ";";
	//下标分割符
	public final static String regex_index = "_";

	/**
	 * 连接数组
	 * 
	 * @param s
	 *            分隔符
	 * @param strs
	 */
	public static String join(String s, String... strs) {
		if (strs.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(strs[0].toString());
		for (int i = 1; i < strs.length; i++) {
			sb.append(s).append(strs[i]);
		}
		return sb.toString();
	}

	/**
	 * 连接数组
	 * 
	 * @param s
	 *            分隔符
	 * @param objects
	 */
	public static String join(String s, Object... objects) {
		if (objects.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(objects[0].toString());
		for (int i = 1; i < objects.length; i++) {
			sb.append(s).append(objects[i]);
		}
		return sb.toString();
	}

	/**
	 * 连接数组
	 * 
	 * @param s
	 *            分隔符
	 * @param objects
	 */
	public static String join(String s, int[] array) {
		if (array.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(array[0] + "");
		for (int i = 1; i < array.length; i++) {
			sb.append(s).append(array[i]);
		}
		return sb.toString();
	}

	/**
	 * 连接数组
	 * 
	 * @param s
	 *            分隔符
	 * @param elements
	 */
	public static String join(String s, Set elements) {
		if (elements == null || elements.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Object obj : elements) {
			sb.append(obj).append(s);
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - s.length(), sb.length());
		}
		return sb.toString();
	}

	/**
	 * 连接数组
	 * 
	 * @param s
	 *            分隔符
	 * @param ls
	 */
	public static String join(String s, List ls) {
		if (ls.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(ls.get(0).toString());
		for (int i = 1; i < ls.size(); i++) {
			sb.append(s).append(ls.get(i));
		}
		return sb.toString();
	}

	/**
	 * 大写首字母
	 */
	public static String upFirst(String str) {
		return str.substring(0, 1).toUpperCase().concat(str.substring(1));
	}

	/**
	 * 首字母大写
	 * 
	 * @author Craig
	 * @param str
	 */
	public static String upFirst1(String str) {
		char[] strs = str.toCharArray();
		if ((strs[0] >= 'a' && strs[0] <= 'z')) {
			strs[0] -= 32;
			return String.valueOf(strs);
		} else {
			return upFirst(str);
		}
	}

	/**
	 * 下划线风格转小写驼峰
	 */
	public static String underlineToLowerCamal(String s) {
		String[] ss = s.split("_");
		for (int i = 1; i < ss.length; i++) {
			ss[i] = upFirst1(ss[i]);
		}
		return join("", ss);
	}

	/**
	 * 下划线风格转大写驼峰
	 */
	public static String underlineToUpperCamal(String s) {
		String[] ss = s.split("_");
		for (int i = 0; i < ss.length; i++) {
			ss[i] = upFirst1(ss[i]);
		}
		return join("", ss);
	}

	/**
	 * 驼峰转下划线,未处理大小写
	 */
	public static String camalToUnderline(String s) {
		StringBuilder sb = new StringBuilder();
		if (s.length() > 0) {
			sb.append(s.charAt(0));
		}
		for (int i = 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append("_");
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 判断字符串为空（null或者""）
	 * 
	 * <pre>
	 * StringUtils.isEmpty(null) = true
	 * StringUtils.isEmpty("") = true
	 * StringUtils.isEmpty(" ") = false
	 * StringUtils.isEmpty("bob") = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/**
	 * 判断字符串不为空（null或者""）
	 * 
	 * <pre>
	 * StringUtils.isNotEmpty(null) = false
	 * StringUtils.isNotEmpty("") = false
	 * StringUtils.isNotEmpty(" ") = true
	 * StringUtils.isNotEmpty("bob") = true
	 * StringUtils.isNotEmpty("  bob  ") = true
	 * </pre>
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return !StringUtils.isEmpty(s);
	}

	/**
	 * 判断字符串为空白字符串(null或者""或者" ")
	 * 
	 * <pre>
	 * StringUtils.isBlank(null) = true
	 * StringUtils.isBlank("") = true
	 * StringUtils.isBlank(" ") = true
	 * StringUtils.isBlank("bob") = false
	 * StringUtils.isBlank(" bob ") = false
	 * </pre>
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isBlank(String s) {
		int strLen;
		if (s == null || (strLen = s.length()) == 0) {
			return true;
		}

		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(s.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串不为空白字符串(null或者""或者" ")
	 * 
	 * <pre>
	 * StringUtils.isNotBlank(null) = false
	 * StringUtils.isNotBlank("") = false
	 * StringUtils.isNotBlank(" ") = false
	 * StringUtils.isNotBlank("bob") = true
	 * StringUtils.isNotBlank(" bob ") = true
	 * </pre>
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotBlank(String s) {
		return !StringUtils.isBlank(s);
	}

	/**
	 * 截取字符串
	 * 
	 * <pre>
	 * StringUtils.substringBefore("ajp_djp_gjp_j", "")  = ""
	 * StringUtils.substringBefore("ajp_djp_gjp_j", null)  = ""
	 * StringUtils.substringBefore("ajp_djp_gjp_j", "jp_")  = "a"
	 * StringUtils.substringBefore("ajp_djp_gjp_j", "jk_")  = "ajp_djp_gjp_j"
	 * </pre>
	 * 
	 * @param str
	 *            被截取的字符串
	 * @param separator
	 *            截取分隔符
	 * @return
	 */
	public static String substringBefore(String str, String separator) {
		if ((isEmpty(str)) || (separator == null)) {
			return str;
		}
		if (separator.isEmpty()) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * 截取字符串
	 * 
	 * <pre>
	 * StringUtils.substringAfter("ajp_djp_gjp_j", "jp_")  = "defjp_ghi"
	 * StringUtils.substringAfter("ajp_djp_gjp_j", "")  = "ajp_djp_gjp_j"
	 * StringUtils.substringAfter("ajp_djp_gjp_j", null)  = "ajp_djp_gjp_j"
	 * StringUtils.substringAfter("ajp_djp_gjp_j", "jk_")  = ""
	 * </pre>
	 * 
	 * @param str
	 *            被截取的字符串
	 * @param separator
	 *            截取分隔符
	 * @return
	 */
	public static String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * 截取字符串
	 * 
	 * <pre>
	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", "")  = "ajp_djp_gjp_j"
	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", null)  = "ajp_djp_gjp_j"
	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", "jk_")  = "ajp_djp_g"
	 * StringUtils.substringBeforeLast("ajp_djp_gjp_j", "jp_")  = "ajp_djp_g"
	 * </pre>
	 * 
	 * @param str
	 *            被截取的字符串
	 * @param separator
	 *            截取分隔符
	 * @return
	 */
	public static String substringBeforeLast(String str, String separator) {
		if ((isEmpty(str)) || (isEmpty(separator))) {
			return str;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * 截取字符串
	 * 
	 * <pre>
	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", "")  = ""
	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", null)  = ""
	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", "jk_")  = ""
	 * StringUtils.substringAfterLast("ajp_djp_gjp_j", "jp_")  = "j"
	 * </pre>
	 * 
	 * @param str
	 *            被截取的字符串
	 * @param separator
	 *            截取分隔符
	 * @return
	 */
	public static String substringAfterLast(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return "";
		}
		int pos = str.lastIndexOf(separator);
		if ((pos == -1) || (pos == str.length() - separator.length())) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * 根据分隔符，分隔字符串为整型数组
	 * 
	 * @param str
	 * @param regex
	 *            分隔字符串
	 * @return
	 */
	public static int[] toIntArray(String str, String regex) {
		if (isEmpty(str) || isEmpty(regex)) {
			return new int[0];
		}

		String[] strInfos = str.split(regex);
		int[] data = new int[strInfos.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = Integer.parseInt(strInfos[i]);
		}
		return data;
	}

	/**
	 * 根据分隔符，分隔字符串为整型数组
	 * 
	 * @param str
	 * @param regex
	 *            分隔字符串
	 * @return
	 */
	public static List<Integer> toIntList(String str, String regex) {
		if (isEmpty(str) || isEmpty(regex)) {
			return new ArrayList<Integer>();
		}

		String[] strInfos = str.split(regex);
		List<Integer> data = new ArrayList<Integer>(strInfos.length);
		for (int i = 0; i < strInfos.length; i++) {
			data.add(Integer.parseInt(strInfos[i]));
		}
		return data;
	}

	/**
	 * 根据分隔符，分隔字符串为字符串
	 * 
	 * @param str
	 * @param regex
	 *            分隔字符串
	 * @return
	 */
	public static List<String> toStringList(String str, String regex) {
		if (isEmpty(str) || isEmpty(regex)) {
			return new ArrayList<String>();
		}
		String[] strInfos = str.split(regex);
		List<String> data = new ArrayList<String>(strInfos.length);
		for (int i = 0; i < strInfos.length; i++) {
			data.add(strInfos[i]);
		}
		return data;
	}

	/**
	 * 根据分隔符，分隔字符串为整型数组
	 * 
	 * @param str
	 * @param separator
	 * @return
	 */
	public static long[] toLongArray(String str, String regex) {
		if (isEmpty(str) || isEmpty(regex)) {
			return new long[0];
		}

		String[] strInfos = str.split(regex);
		long[] data = new long[strInfos.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = Long.parseLong(strInfos[i]);
		}
		return data;
	}

	/**
	 * 根据分隔符，分隔字符串为整型数组
	 * 
	 * @param str
	 * @param separator
	 * @return
	 */
	public static List<Long> toLongList(String str, String regex) {
		if (isEmpty(str) || isEmpty(regex)) {
			return new ArrayList<Long>();
		}

		String[] strInfos = str.split(regex);
		ArrayList<Long> data = new ArrayList<Long>(strInfos.length);
		for (int i = 0; i < strInfos.length; i++) {
			data.add(Long.parseLong(strInfos[i]));
		}
		return data;
	}

	/**
	 * 分割字符串成map
	 * 键值对之前用":"分隔
	 * K V 之间用"_"分隔
	 * 
	 * @param str
	 * @return
	 */
	public static Map<Integer, Integer> splitEntryStr(String str) {
		return splitEntryStr(str, ":", "_");
	}

	/**
	 * 分割字符串成map
	 *
	 * 键值对之前用";"分隔
	 * K V 之间用"_"分隔
	 *
	 * @param str
	 * @return
	 */
	public static Map<Integer, Integer> splitEntryStrBySemicolon(String str) {
		return splitEntryStr(str, regex, regex_index);
	}

	/**
	 * 分割字符串成map
	 * 键值对之间用 regex1 分隔
	 * K V 之间用 regex2 分隔
	 * 
	 * @param str
	 * @param regex1
	 * @param regex2
	 * @return 返回的LinkedHashMap，字符串为空则返回空map
	 */
	public static Map<Integer, Integer> splitEntryStr(String str, String regex1, String regex2) {
		Map<Integer, Integer> result = new LinkedHashMap<>();
		if (StringUtils.isBlank(str)) {
			return result;
		}

		String[] split = str.split(regex1);
		for (String entryStr : split) {
			String[] entry = entryStr.split(regex2);
			if (entry.length < 2) {
				continue;
			}

			result.put(Integer.valueOf(entry[0]), Integer.valueOf(entry[1]));
		}

		return result;
	}

	/**
	 * 分割字符串成map
	 * 键值对之间用","分隔
	 * K V 之间用":"分隔
	 * 
	 * @param str
	 * @param regex1
	 * @param regex2
	 * @return 返回的HashMap，字符串为空则返回空map
	 */
	public static Map<String, Integer> splitToMap(String str) {
		Map<String, Integer> result = new HashMap<>();
		if (StringUtils.isBlank(str)) {
			return result;
		}

		String[] split = str.split(",");
		for (String entryStr : split) {
			String[] entry = entryStr.split(":");
			if (entry.length < 2) {
				continue;
			}

			result.put(entry[0], Integer.valueOf(entry[1]));
		}

		return result;
	}

	public static String collection2String(Collection<Long> collection) {
		if (collection == null || collection.size() < 1) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		for (Long friendId : collection) {
			builder.append(friendId).append("_");
		}
		
		builder.delete(builder.length() - 1, builder.length());
		return builder.toString();
	}
	
	/**
	 * 把map拼接为字符串
	 * 键值对之间用","分隔
	 * K V 之间用":"分隔
	 * 
	 * @param str
	 * @param regex1
	 * @param regex2
	 * @return 返回的LinkedHashMap，字符串为空则返回空map
	 */
	public static <K, V> String joinFromMap(Map<K, V> map) {
		if (map.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder(map.size() * 10);
		for (Map.Entry<K, V> node : map.entrySet()) {
			sb.append(node.getKey()).append(':').append(node.getValue());
			sb.append(',');
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		String aa = "ajp_djp_gjp_j";
		String bb = "jk_";
		String substringAfter = StringUtils.substringAfter(aa, bb);
		String substringBefore = StringUtils.substringBefore(aa, bb);
		String substringBeforeLast = StringUtils.substringBeforeLast(aa, bb);
		String substringAfterLast = StringUtils.substringAfterLast(aa, bb);
		System.out.println("substringBefore = " + substringBefore);
		System.out.println("substringAfter = " + substringAfter);
		System.out.println("substringBeforeLast = " + substringBeforeLast);
		System.out.println("substringAfterLast = " + substringAfterLast);
	}
}
