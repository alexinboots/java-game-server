package net.db.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.db.dao.CachedDao;

/**
 * 缓存注解，使用这个注解的类会自动使用cachedDao来做二级缓存
 * 如使用此接口，请在id() idValues()函数也按照key subkey顺序填写
 *
 * @see CachedDao
 *
 * @author ckf
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {

	/**返回对象的key*/
	String key();

	/**返回对象的subkey*/
	String subkey() default "";

	/**一对多关系中初始化的sql*/
	String manyInitSql() default "";

}
