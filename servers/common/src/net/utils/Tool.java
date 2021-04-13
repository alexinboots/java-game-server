package net.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import com.alibaba.fastjson.JSONObject;

/**
 * 游戏工具类
 * 
 * 例如 常用的静态功能方法，画图方法
 * 
 * @author lolex
 *
 */
public class Tool {

	private static Logger logger = LoggerFactory.getLogger(Tool.class);

	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static int calcCrc(byte[] bytes, int offset, int size, int crc) {
		int hash = crc;
		for (int i = offset; i < size; i++) {
			hash ^= ((i & 0x1) == 0 ? hash << 7 ^ bytes[i] & 0xFF ^ hash >>> 3
					: hash << 11 ^ bytes[i] & 0xFF ^ hash >>> 5 ^ 0xFFFFFFFF);
		}
		return hash;
	}

	public static int calcCrc(byte[] bytes, int crc) {
		return calcCrc(bytes, 0, bytes.length, crc);
	}

	public static int calcCrc(byte[] bytes) {
		return calcCrc(bytes, 0);
	}

	public static int hashString(String string) {
		int hash = 0;
		for (int i = 0; i < string.length(); i++) {
			hash ^= ((i & 0x1) == 0 ? hash << 7 ^ string.charAt(i) ^ hash >> 3
					: hash << 11 ^ string.charAt(i) ^ hash >> 5 ^ 0xFFFFFFFF);
		}
		return hash & 0x7FFFFFFF;
	}

	public static String bytesToHexString(byte[] data) {
		char[] chars = new char[data.length * 2];
		for (int i = 0; i < data.length; i++) {
			chars[(i * 2 + 0)] = hexDigits[(data[i] >>> 4 & 0xF)];
			chars[(i * 2 + 1)] = hexDigits[(data[i] & 0xF)];
		}
		return new String(chars);
	}

	public static void sleep(long time) {
		if (time <= 0) {
			return;
		}
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			Tool.catchException(e);
		}
	}

	public static void safeCloseInputStream(InputStream ips) {
		if (ips == null) {
			return;
		}

		try {
			ips.close();
		} catch (Exception e) {
			Tool.catchException(e);
		}
	}

	public static void safeCloseOutputStream(OutputStream ops) {
		if (ops == null) {
			return;
		}

		try {
			ops.close();
		} catch (Exception e) {
			Tool.catchException(e);
		}
	}

	public static long getProcessId() {
		try {
			String processName = ManagementFactory.getRuntimeMXBean().getName();
			return Long.parseLong(processName.split("@")[0]);
		} catch (Exception e) {
			Tool.catchException(e);
		}
		return 0L;
	}

	public static long getThreadId() {
		long threadId = Thread.currentThread().getId();
		return threadId;
	}

	public static void printOsEnv() {
		Properties props = System.getProperties();
		System.out.println("Os: " + props.getProperty("os.name") + ", Arch: " + props.getProperty("os.arch")
				+ ", Version: " + props.getProperty("os.version"));

		String userDir = System.getProperty("user.dir");
		System.out.println("UserDir: " + userDir);

		String homeDir = System.getProperty("java.home");
		System.out.println("JavaHome: " + homeDir);
	}

	public static boolean isWindowsOS() {
		Properties props = System.getProperties();
		if (props.getProperty("os.name").toLowerCase().contains("windows")) {
			return true;
		}
		return false;
	}

	/**
	 * 将-128,127的byte转换成0,255的int整数
	 * 
	 * @param b
	 * @return
	 */
	public static short byte2short(byte b) {
		//return (short) (b < 0 ? 256 + b : b);
		return (short) (b & 0xff);
	}

	public static int parseInt(String text, int defaultValue) {

		if (text == null || text.length() <= 0)
			return defaultValue;
		try {
			return Integer.parseInt(text.trim());
		} catch (Exception ex) {
		}
		return defaultValue;
	}

	public static int parseInt(String text) {
		return parseInt(text, 0);
	}

	public static int parseInt16(String text, int defaultValue) {

		if (text == null || text.length() <= 0)
			return defaultValue;
		try {
			return Integer.parseInt(text.trim(), 16);
		} catch (Exception ex) {
		}
		return defaultValue;
	}

	public final static String[] split(String msg, String sep) {
		if (msg == null || "".equals(msg) || sep == null || "".equals(sep)) {
			return null;
		}
		Vector list = new Vector();
		int index = msg.indexOf(sep);
		while (index >= 0) {
			String str = msg.substring(0, index);
			list.addElement(str);
			msg = msg.substring(index + 1);
			index = msg.indexOf(sep);
		}
		list.addElement(msg);
		String[] tempResult = new String[list.size()];
		int idx = 0;
		String tempStr = null;
		for (int i = 0; i < list.size(); i++) {
			tempStr = (String) list.elementAt(i);
			if (tempStr == null || "".equals(tempStr)) {
				continue;
			}
			tempResult[idx] = tempStr;
			idx++;
		}

		String[] result = new String[idx];
		System.arraycopy(tempResult, 0, result, 0, idx);
		return result;
	}

	/**
	 * 获得2个矩形区域的交集
	 * 
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param x2
	 * @param y2
	 * @param w2
	 * @param h2
	 * @return 交集数据 ,交集矩形的X,Y,Width,Height
	 */
	public static int[] rectGetIntersection(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {

		int x = x1;
		if (x2 > x1) {
			x = x2;
		}

		int y = y1;
		if (y2 > y1) {
			y = y2;
		}

		int endX1 = x1 + w1;
		int endX2 = x2 + w2;

		int endX = endX1;
		if (endX2 < endX1) {
			endX = endX2;
		}

		int endY1 = y1 + h1;
		int endY2 = y2 + h2;

		int endY = endY1;
		if (endY2 < endY1) {
			endY = endY2;
		}

		int w = endX - x;
		int h = endY - y;

		if (w < 0 || h < 0) {
			w = 0;
			h = 0;
		}
		return new int[] { x, y, w, h };
	}

	/**
	 * 常用方法; 判断2个矩形框是否碰撞;
	 * 
	 * 主要用于判断精灵/精灵切块 是否在 屏幕内;
	 * 
	 * @param _x1
	 * @param _y1
	 * @param _w1
	 * @param _h1
	 * @param _x2
	 * @param _y2
	 * @param _w2
	 * @param _h2
	 * @return
	 */
	public final static boolean isColliding(int _x1, int _y1, int _w1, int _h1, int _x2, int _y2, int _w2, int _h2) {
		if (_x1 + _w1 <= _x2)
			return false;
		if (_x1 >= _x2 + _w2)
			return false;
		if (_y1 + _h1 <= _y2)
			return false;
		if (_y1 >= _y2 + _h2)
			return false;
		return true;
	}

	/**
	 * 常用方法，判断点是否(pointx,pointy)在Rect(x,y,w,h)区域内
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param pointx
	 * @param pointy
	 * @return
	 */
	public final static boolean rectIn(int x, int y, int w, int h, int pointx, int pointy) {
		if (x > pointx || y > pointy) {
			return false;
		}
		if ((x + w) < pointx) {
			return false;
		}
		if ((y + h) < pointy) {
			return false;
		}
		return true;
	}

	public static boolean rectIntersect(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		return Tool.isColliding(x1, y1, w1, h1, x2, y2, w2, h2);
	}

	/**
	 * (x1,y1,w1,h1)是否完全包含(x2,y2,w2,h2)
	 * 
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param x2
	 * @param y2
	 * @param w2
	 * @param h2
	 * @return
	 */
	public static boolean rectContain(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if (x1 > x2) {
			return false;
		}
		int i2;
		int j2;
		i2 = x1 + w1;
		j2 = x2 + w2;
		if (i2 < j2 || y1 > y2) {
			return false;
		}
		int k2;
		i2 = y1 + h1;
		k2 = y2 + h2;
		if (i2 < k2) {
			return false;
		}
		return true;
	}

	// ============================================================================== //
	// 数组操作
	public static short getShort(byte[] arr, int idx) {

		if (arr == null || (idx + 1 >= arr.length)) {
			return 0;
		}
		int v = arr[idx] & 0xff;
		v = (v << 8) | (arr[idx + 1] & 0xFF);
		return (short) v;
	}

	public static int getInt(byte[] arr, int idx) {

		if (arr == null || (idx + 3 >= arr.length)) {
			return 0;
		}

		int v = arr[idx] & 0xff;
		v = (v << 8) | (arr[idx + 1] & 0xFF);
		v = (v << 8) | (arr[idx + 2] & 0xFF);
		v = (v << 8) | (arr[idx + 3] & 0xFF);
		return v;
	}

	public static int getLong(byte[] arr, int idx) {

		if (arr == null || (idx + 7 >= arr.length)) {
			return 0;
		}

		int v = arr[idx] & 0xff;
		v = (v << 8) | (arr[idx + 1] & 0xFF);
		v = (v << 8) | (arr[idx + 2] & 0xFF);
		v = (v << 8) | (arr[idx + 3] & 0xFF);
		v = (v << 8) | (arr[idx + 4] & 0xFF);
		v = (v << 8) | (arr[idx + 5] & 0xFF);
		v = (v << 8) | (arr[idx + 6] & 0xFF);
		v = (v << 8) | (arr[idx + 7] & 0xFF);
		return v;
	}

	public static String getUTF(byte[] datas, int offset) {
		if (datas == null || offset >= datas.length - 2) {
			return null;
		}

		int index = offset;
		short length = getShort(datas, index);
		index += 2;
		if (index + length > datas.length) {
			return null;
		}

		try {
			return new String(datas, index, length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(datas, index, length);
		}
	}

	// ============================================================================== //

	public static Random random = new Random();

	/**
	 * rand(int range) only return [0-range) exclude range. Maximum is
	 * (range-1), no check on negative range, don't try. It is similar to
	 * CLDC1.1 ran.nextInt(n)
	 * 
	 * @param range
	 * @return
	 */
	public final static int rand(int range) {
		if (range == 0)
			return 0;
		// assert range>0
		// 0 may have division by zero error.
		return Math.abs(random.nextInt() % range);
	}

	/**
	 * random producer
	 * 
	 * @param start
	 *            the start point
	 * @param end
	 *            the end point
	 * @return the random value，scope is[start,end]；
	 */
	public final static int rand(int start, int end) {
		int k1 = random.nextInt();
		if (k1 < 0)
			k1 = -k1;
		return k1 % ((end - start) + 1) + start;
	}

	public static int[] getCopyData(int[] data) {

		int[] copyData = null;

		if (data != null) {
			copyData = new int[data.length];
			System.arraycopy(data, 0, copyData, 0, data.length);
		}
		return copyData;
	}

	public static int getOffsetValue(int pixel, int totalSize, int screenSize, int screenSizeC) {

		int dis;
		//地图比屏幕宽;
		if (totalSize > screenSize) {
			dis = screenSizeC - pixel;
			//中间以左
			if (dis > 0)
				dis = 0;
			//中间以右
			else if (dis < screenSize - totalSize)
				dis = screenSize - totalSize;
		} else {
			dis = (screenSize - totalSize) / 2;
		}
		return dis;
	}

	public static final int entropy(int _cur, int _target) {
		return entropy(_cur, _target, false);
	}

	public static final int entropy(int _cur, int _target, boolean isAdjust) {
		if (_cur != _target) {
			if (isAdjust && Math.abs(_cur - _target) < 30) {
				_cur = _target;
			} else {
				int m_oldH = _cur;

				//    			_cur = (_cur * 3 + _target) >> 2;
				//_cur = (_cur * speed1 + _target) >> speed2;

				_cur = (_cur * 3 + _target * 2) / 5;

				//    			_cur += ((_target-_cur) * 2) / 5;

				if (m_oldH == _cur) {
					_cur = _target;
				}
			}
		}
		return _cur;
	}

	/**
	 * Function name:sqrt Description: 简单开平方
	 * 
	 * @param n
	 * @return
	 */
	public static int sqrt(int n) {
		int r, l, t; //r: 方根; l: 余数; t: 试除数; 
		if (n < 100) {
			r = 9;
			while (n < r * r)
				r--;
		} else {
			r = sqrt(n / 100);
			l = n - r * r * 100;
			t = l / (r * 20);
			while (t * (r * 20 + t) > l) {
				t--;
			}
			r = r * 10 + t;
		}
		return r;
	}

	public static int getCost(int x1, int y1, int x2, int y2) {
		// 获得坐标点间差值 公式：(x1, y1)-(x2, y2)
		int m = Math.abs(x1 - x2);
		int n = Math.abs(y1 - y2);
		// 取两节点间欧几理德距离（直线距离）做为估价值，用以获得成本
		return sqrt(m * m + n * n);
	}

	// ---------------------- (x,y) Key 相关 ----------------- //
	// 如果要使用负数的话：getXKey,getYKey要 int -> short
	public static int setKeyXY(int x, int y) {
		return (x & 0xffff) | ((y & 0xffff) << 16);
	}

	public static int getXKey(int key) {
		return (key & 0xffff);
	}

	public static int getYKey(int key) {
		return ((key >> 16) & 0xffff);
	}

	// ----------------------------------------------------- //

	public static String appendString(String[] strs, int startIndex, int endIndex) {
		StringBuffer sb = new StringBuffer();

		if (strs != null) {
			for (int i = startIndex; i < endIndex; i++) {

				if (i < 0 || i >= strs.length) {
					continue;
				}

				if (strs[i] == null) {
					continue;
				}
				sb.append(strs[i]);
			}
		}

		return sb.toString();
	}

	public static void debug(Object object) {
		if (object == null) {
			return;
		}
		//		if(LogicCommon.isOperTest==true){
		//			logger.info("DEBUG: " + object.toString());
		//		}
	}

	public static String join(short[] list, String sep) {
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			return "";
		}

		for (int i = 0; i < list.length; i++) {
			sb.append(list[i]);
			sb.append(sep);
		}

		return sb.toString();
	}

	public static String join(byte[] list, String sep) {
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			return "";
		}

		for (int i = 0; i < list.length; i++) {
			sb.append(list[i]);
			sb.append(sep);
		}

		return sb.toString();
	}

	public static String join(int[] list, String sep) {
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			return "";
		}

		for (int i = 0; i < list.length; i++) {
			sb.append(list[i]);
			sb.append(sep);
		}

		return sb.toString();
	}

	/**
	 * 实用方法, 打印数组内容
	 * 
	 * @param objects
	 * @param objectName
	 */
	public static void printArray(Object[] objects, String objectName) {
		if (objects == null) {
			return;
		}

		for (int i = 0; i < objects.length; i++) {
			debug(objectName + "[" + i + "]=" + objects[i]);
		}
	}

	public static boolean isEmulator() {
		//System.getProperty("microedition.platform") //获得手机平台
		byte byte0 = 0;
		try {
			if (Class.forName("java.applet.Applet") != null) {
				byte0 = 1;
			}
		} catch (Exception exception) {
		}
		try {
			if (byte0 == 0 && Class.forName("emulator.Emulator") != null) {
				byte0 = 2;
			}
		} catch (Exception exception1) {
		}
		//手机顽童 内存=8000000 
		if (byte0 == 0 && Runtime.getRuntime().totalMemory() - 0x3001dbL == 0x4a1025L)
			byte0 = 3;
		return byte0 != 0;
	}

	/**
	 * 基础值+加成值，检查上下限返回
	 * 
	 * @param base
	 *            基础值
	 * @param add
	 *            加成值
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 * @return
	 */
	public static int sumValue(int base, int add, int min, int max) {

		int tepValue = base + add;

		if (base > 0 && add > 0 && tepValue <= 0) {//越界了,使用最大值
			tepValue = max;
		}

		if (tepValue < min) {
			tepValue = min;
		}

		if (tepValue > max) {
			tepValue = max;
		}

		return tepValue;
	}

	/**
	 * 按百分比计算，统一计算接口，避免溢出
	 * 
	 * @param base
	 *            原始值
	 * @param scaleAdd
	 *            百分比的整数，数值范围大于0
	 * @return 加成后的数值（向下取整，数值范围[0,int.max]）
	 */
	public static int scaleValue100(int baseValue, int scaleAdd) {
		//先计算出变化的比例，则相乘，尽量减少溢出的机会
		double result = baseValue * (1 + scaleAdd / 100.0);
		if (result > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else {
			return (int) result;
		}
	}

	/**
	 * 按万分比计算，统一计算接口，避免溢出
	 * 
	 * @param base
	 *            原始值
	 * @param scaleAdd
	 *            万分比的整数，，数值范围大于0
	 * @return 加成后的数值（向下取整，数值范围[0,int.max]）
	 */
	public static int scaleValue10000(int baseValue, int scaleAdd) {
		//先计算出变化的比例，则相乘，尽量减少溢出的机会
		double result = baseValue * (1 + scaleAdd / 10000.0);

		if (result > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else {
			return (int) result;
		}
	}

	//	/**
	//	 * 通过技能ID和技能等级的逻辑计算获取技能惟一的Key
	//	 * 
	//	 * @param skillID
	//	 * @param level
	//	 * @return
	//	 */
	//	public final static Integer getSkillKey(int skillID, byte level) {
	//		return new Integer(((level) << 24 | skillID & 0x00FFFFFF));
	//	}

	/**
	 * 通过一个集合String封装一个String数组
	 * 
	 * @param vector
	 * @return
	 */
	public static final String[] getStringArrayByVector(Vector vector) {
		if (vector == null) {
			return null;
		}
		int size = vector.size();
		if (size <= 0) {
			return null;
		}

		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			String str = (String) vector.elementAt(i);
			if (str == null) {
				continue;
			}

			array[i] = str;
		}
		return array;
	}

	/**
	 * 判断是否数组越界
	 * 
	 * @param index
	 * @param array
	 * @return
	 */
	public static boolean isArrayIndexOutOfBounds(int index, Object arrayObject) {

		if (arrayObject == null) {
			return true;
		}

		int length = 0;
		if (arrayObject instanceof byte[]) {
			length = ((byte[]) arrayObject).length;
		} else if (arrayObject instanceof short[]) {
			length = ((short[]) arrayObject).length;
		} else if (arrayObject instanceof int[]) {
			length = ((int[]) arrayObject).length;
		} else if (arrayObject instanceof String[]) {
			length = ((String[]) arrayObject).length;
		} else if (arrayObject instanceof Vector) {
			length = ((Vector) arrayObject).size();
		}

		if (index < 0 || index >= length) {
			return true;
		}

		return false;
	}

	/**
	 * 获得位数的掩码值 例如 1位 = 0x1; 2位 = 0x3; 3位 = 0x7;
	 * 
	 * @param bitNum
	 * @return
	 */
	public static int getMaskBitValue(int bitNum) {

		if (bitNum >= 32) {
			return 0xFFFFFFFF;
		}

		int value = 0;
		for (int i = 0; i < bitNum; i++) {
			value |= 1 << i;
		}
		return value;
	}

	/**
	 * 判断字符串是否为NULL | 空
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isNullText(String text) {

		if (text == null) {
			return true;
		}

		if (text.trim().equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 位操作运算(设置值)
	 * 
	 * @param flag
	 *            true表示设1,false表示设0
	 * @param index
	 *            第几位
	 * @param value
	 *            设置的值
	 * @return
	 */
	public static int setBit(boolean flag, int index, int value) {
		if (flag) {
			value |= index;
		} else {
			value &= ~index;
		}
		return value;
	}

	/**
	 * 位操作运算(判断值)
	 * 
	 * @param index
	 *            第几位
	 * @param value
	 *            判断的值
	 * @return
	 */
	public static boolean isBit(int index, int value) {
		return (value & index) != 0;
	}

	/**
	 * 获得掩码的位数（最多8位） 例如 1111 = 4位
	 * 
	 * @param maskValue
	 * @return
	 */
	public static int getBitNum(int maskValue) {

		int bitNum = 0;
		for (int i = 0; i < 8; i++) {
			int bitValue = 1 << i;
			if (isBit(bitValue, maskValue)) {
				bitNum++;
			}
		}

		return bitNum;
	}

	/**
	 * 
	 * Function name:getSubList Description: 用于对一个列表进行分页处理
	 * 
	 * @param <T>
	 * @param orgList
	 *            : 源列表
	 * @param pageIndex
	 *            ：页码
	 * @param pageSize
	 *            ：分页条目数
	 * @param subList
	 *            ：分页后的列表
	 * @return：返回结果(-1分页参数异常 >=0总的页数)
	 */
	public static <T> List<T> getSubList(List<T> orgList, int pageIndex, int pageSize) {
		List<T> subList = new ArrayList<T>();
		if (pageIndex <= 0) {//页面上限超了，返回第一页
			pageIndex = 1;
		}

		if (pageSize <= 0 || orgList.size() == 0) {
			return subList;
		}

		int pageTotalNum = (orgList.size() % pageSize) == 0 ? (orgList.size() / pageSize)
				: ((orgList.size() / pageSize) + 1);
		if (pageTotalNum < pageIndex) {//页码超了，返回最后一页
			pageIndex = pageTotalNum;
		}

		int start = pageSize * (pageIndex - 1);
		int end = pageSize * pageIndex;
		end = end > orgList.size() ? orgList.size() : end;
		subList.addAll(orgList.subList(start, end));
		return subList;
	}

	/**
	 * 获取ip
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIp(ChannelHandlerContext ctx) {
		String ipAdd = null;
		try {
			ipAdd = ctx.getChannel().getRemoteAddress().toString();
			//ipAdd=/192.168.1.129:2468
			for (int i = 0; i < ipAdd.length(); i++) {
				char ch = ipAdd.charAt(i);
				if (ch >= '0' && ch <= '9') {//找到第一个数字
					int end = ipAdd.indexOf(":");
					ipAdd = ipAdd.substring(i, end);
					break;
				}
			}
		} catch (Exception e2) {
			ipAdd = "";//不知什么鸟ip
			//e2.printStackTrace();
		}

		return ipAdd;
	}

	/**
	 * 获取保存在ctx里的语言版本参数,第一次连接校验时赋值
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getIntObjFromCtx(ChannelHandlerContext ctx, String key) {
		int vol = 0;
		try {
			HashMap<String, Object> context = (HashMap<String, Object>) ctx.getAttachment();
			vol = (Integer) context.get(key);
		} catch (Exception e) {
			logger.error("getIntObjFromCtx错误,key=" + key, e);
		}

		return vol;
	}

	/**
	 * 获取保存在ctx里的语言版本参数,第一次连接校验时赋值
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getStrObjFromCtx(ChannelHandlerContext ctx, String key) {
		String vol = "";
		try {
			HashMap<String, Object> context = (HashMap<String, Object>) ctx.getAttachment();
			vol = (String) context.get(key);
		} catch (Exception e) {
			logger.error("getIntObjFromCtx错误,key=" + key, e);
		}

		return vol;
	}

	/**
	 * 检查list里是否有相同的元素
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isHaveSameIdInList(List<Long> list) {
		List<Long> tempList = new ArrayList<>();
		for (Long long1 : list) {
			if (tempList.contains(long1)) {
				return true;
			} else {
				tempList.add(long1);
			}
		}

		return false;
	}

	/**
	 * int to intlist
	 * 
	 * @param i
	 * @return
	 */
	public static List<Integer> toList(int i) {
		List<Integer> list = new ArrayList<Integer>();
		if (i == 0) {
			return list;
		}

		while (true) {
			if (i % 10 > 0) {
				list.add(i % 10);
				i /= 10;
			} else {
				break;
			}
		}
		return list;
	}

	/**
	 * Function name:getRmiClient Description: 取得rmi实例
	 * 
	 * @param url
	 *            :远程服务器地址,如:"rmi://127.0.0.1:3000/dbServer"
	 * @return
	 */
	public static Object getRmiClient(String url, Class c) {
		RmiProxyFactoryBean factory = new RmiProxyFactoryBean();
		factory.setServiceInterface(c);
		factory.setServiceUrl(url);//"rmi://127.0.0.1:3000/dbServer"
		factory.afterPropertiesSet();
		factory.setRefreshStubOnConnectFailure(true);
		return factory.getObject();
	}

	/**
	 * json字符串转为Map对象
	 * 
	 * @param json
	 * @return
	 */
	public static Map jsonToMap(String json) {
		JSONObject jsonObject = JSONObject.parseObject(json);
		if (jsonObject == null) {
			return null;
		}
		Map valueMap = new HashMap();
		Set<String> keySet = jsonObject.keySet();
		for (String key : keySet) {
			valueMap.put(key, jsonObject.get(key));
		}
		return valueMap;
	}

	/**
	 * map对象转为json字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToString(Map map) {
		String jsonString = JSONObject.toJSONString(map);
		return jsonString.toString();
	}

	/**
	 * 捕获异常
	 * 
	 * @param ex
	 */
	public static void catchException(Exception ex) {
		ex.printStackTrace();
	}

	/**
	 * 从参数数组中获取一个整型参数
	 * 
	 * @param args
	 *            参数数组
	 * @param idx
	 *            位于数组中的索引
	 * @param defaultVal
	 *            如果找不到，返回的默认值
	 * @return
	 */
	public static int getIntParam(String[] args, int idx, int defaultVal) {
		int val = args.length > idx ? Integer.parseInt(args[idx].trim()) : defaultVal;
		return val;
	}

	/**
	 * 从参数数组中获取一个布尔参数
	 * 
	 * @param args
	 *            参数数组
	 * @param idx
	 *            位于数组中的索引
	 * @param defaultVal
	 *            如果找不到，返回的默认值
	 * @return
	 */
	public static boolean getBoolParam(String[] args, int idx, boolean defaultVal) {
		boolean val = (args.length > idx) ? Boolean.parseBoolean(args[idx].trim()) : defaultVal;
		return val;
	}

	/**
	 * 从参数数组中获取一个字符串参数
	 * 
	 * @param args
	 *            参数数组
	 * @param idx
	 *            位于数组中的索引
	 * @param defaultVal
	 *            如果找不到，返回的默认值
	 * @return
	 */
	public static String getStringParam(String[] args, int idx, String defaultVal) {
		String val = args.length > idx ? args[idx] : defaultVal;
		return val;
	}
}
