package net.utils;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Json转换工具
 * 
 * @author ckf
 *
 */
public class JsonUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	/**
	 * jackson ObjectMapper
	 */
	private static final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * jackson TypeFactory
	 */
	private static final TypeFactory typeFactory = TypeFactory.defaultInstance();
	
	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * 取得jackson ObjectMapper
	 * @return ObjectMapper
	 */
	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	/**
	 * 对象转换成json字符串
	 * @param obj Object
	 * @return String
	 */
	public static String object2String(Object obj) {
		if (obj == null) {
			return null;
		}

		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("对象转换成json字符串异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * 数组转换成json字符串
	 * @param obj
	 * @return
	 */
	public static String array2String(Object ...objs) {
		if (objs == null) {
			return null;
		}

		try {
			return mapper.writerWithType(Object[].class).writeValueAsString(objs);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("对象转换成json字符串异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * json字符串转换成对象(转换的类构造函数需要无参,否则会报错)
	 * @param jsonString String
	 * @param valueType 对象类型
	 * @return T
	 */
	public static <T> T string2Object(String jsonString, Class<T> valueType) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, valueType);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("json字符串[{}]转换成对象异常: {}", jsonString, ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * json字符串转换成对象
	 * @param jsonString String
	 * @param valueTypeRef 对象类型
	 * @return T
	 */
	public static <T> T string2Object(String jsonString, TypeReference<T> valueTypeRef) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, valueTypeRef);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("json字符串[{}]转换成对象异常: {}", jsonString, ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		} 
	}
	
	/**
	 * 对象转换成字节数组
	 * @param obj Object
	 * @return byte[]
	 */
	public static byte[] object2Bytes(Object obj) {
		try {
			return mapper.writeValueAsBytes(obj);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("对象转换成字节数组异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * 字节数组转换成对象
	 * @param data  字节数组
	 * @param valueType JavaType
	 * @return Object
	 */
	public static Object bytes2Object(byte[] data, JavaType valueType) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		try {
			return mapper.readValue(data, 0, data.length, valueType);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("字节数组转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}		
	}
	
	/**
	 * 字节数组转换成对象
	 * @param data 字节数组
	 * @param valueType Type
	 * @return Object
	 */
	public static Object bytes2Object(byte[] data, Type valueType) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		try {
			return mapper.readValue(data, 0, data.length, typeFactory.constructType(valueType));
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("字节数组转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}		
	}
	
	/**
	 * 字节数组转换成对象
	 * @param data 字节数组
	 * @param valueTypeRef TypeReference
	 * @return Object
	 */
	public static <T> T bytes2Object(byte[] data, TypeReference<T> valueTypeRef) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		try {
			return mapper.readValue(data, valueTypeRef);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("字节数组转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}		
	}
	
	/**
	 * 输入流转换成对象
	 * @param src InputStream
	 * @param valueType JavaType
	 * @return T
	 */
	public static <T> T readValue(InputStream src, JavaType valueType) {
		try {
			return mapper.readValue(src, valueType);
		} catch (Exception ex) {
			FormattingTuple message = MessageFormatter.format("输入流转换成对象异常: {}", ex.getMessage());
			logger.error(message.getMessage(), ex);
			return null;
		}
	}
	
	/**
	 * 构建集合类型
	 * @param collectionClass 集合类型
	 * @param elementClass Class
	 * @return CollectionType
	 */
	@SuppressWarnings("rawtypes")
	public static CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
		return typeFactory.constructCollectionType(collectionClass, elementClass);
	}
}
