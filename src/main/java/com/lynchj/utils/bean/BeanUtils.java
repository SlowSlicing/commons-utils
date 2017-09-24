package com.lynchj.utils.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.lynchj.enums.ErrorStatusEnum;
import com.lynchj.exception.LynchjException;

/**
 * 关于操作Bean属性的工具
 * 	Copy 相同Bean的属性
 *
 * @author 大漠知秋
 * 
 * 
 * <dependency>
 *	<groupId>org.springframework</groupId>
 * 	<artifactId>spring-beans</artifactId>
 * 	<version>4.3.7.RELEASE</version>
 * </dependency>
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

	public BeanUtils() {
		throw new LynchjException(ErrorStatusEnum.CREATE_UTILS_ENTITY_ERROR);
	}
	
	/**
	 * 拷贝相同属性的类的属性
	 * 	主要用于VO和Entity之间进行转换
	 *  不拷贝原属性为Null的属性
	 * 
	 * @param target 目标对象
	 * @param source 原对象
	 * @return Copy完毕的目标对象
	 */
	public static <T, S> T copyNotNullProperties(T target, S source) {
		
		copyProperties(target, source, false);
		return null;
		
	}
	
	/**
	 * 拷贝相同属性的类的属性
	 * 	主要用于VO和Entity之间进行转换
	 *  不拷贝原属性为Null的属性
	 * 
	 * @param target 目标对象
	 * @param source 原对象
	 * @return Copy完毕的目标对象
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T, S> T copyNotNullProperties(Class<T> clazzTarget, S source) throws InstantiationException, IllegalAccessException {
		
		T target = clazzTarget.newInstance();
		copyProperties(target, source, false);
		return null;
		
	}
	
	/**
	 * 拷贝相同属性的类的属性
	 * 	主要用于VO和Entity之间进行转换
	 *  拷贝原属性为Null的属性
	 * 
	 * @param target 目标对象
	 * @param source 原对象
	 * @return Copy完毕的目标对象
	 */
	public static <T, S> T copyNullProperties(T target, S source) {
		
		copyProperties(target, source, true);
		return null;
		
	}
	
	/**
	 * 拷贝相同属性的类的属性
	 * 	主要用于VO和Entity之间进行转换
	 *  拷贝原属性为Null的属性
	 * 
	 * @param target 目标对象
	 * @param source 原对象
	 * @return Copy完毕的目标对象
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T, S> T copyNullProperties(Class<T> clazzTarget, S source) throws InstantiationException, IllegalAccessException {
		
		T targer = clazzTarget.newInstance();
		copyProperties(targer, source, true);
		return null;
		
	}
	
	/**
	 * 拷贝相同属性的类的属性
	 * 	主要用于VO和Entity之间进行转换
	 * 
	 * @param target 目标对象
	 * @param source 原对象
	 * @param isCopyNull 是否拷贝为原对象属性为null的属性
	 */
	public static <T, S> void copyProperties(T target, S source, boolean isCopyNull) {
		
		if (null == target && source == null) {
			throw new LynchjException(ErrorStatusEnum.NULL_VALUE_ERROR);
		}
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() != null) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null && sourcePd.getReadMethod() != null) {
                    try {
                        Method readMethod = sourcePd.getReadMethod();
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        Object value = readMethod.invoke(source);
                        // 这里判断以下value是否为空 当然这里也能进行一些特殊要求的处理 例如绑定时格式转换等等
                        if (value != null || isCopyNull) {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        }
                    } catch (Throwable ex) {
                        throw new LynchjException(ErrorStatusEnum.SYS_ERROR);
                    }
                }
            }
        }
        
	}
	
}
