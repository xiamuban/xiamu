package com.xiamu.spring.annotation;

import com.xiamu.spring.enumeration.LimitKeyTypeEnum;

import java.lang.annotation.*;

/**
 * @Author xianghui.luo
 * @Date 2025/2/7 14:18
 * @Description 访问控制自定义注解
 */
@Inherited //指定被修饰的Annotation将具有继承性,说明子类可以继承父类中的该注解
@Documented //说明该注解可以被生成在Javadoc中
/* @Target：描述注解的使用位置
    1、@Target(ElementType.TYPE) //接口、类
    2、@Target(ElementType.FIELD) //属性
    3、@Target(ElementType.METHOD) //方法
    4、@Target(ElementType.PARAMETER) //方法参数
    5、@Target(ElementType.CONSTRUCTOR) //构造函数
    6、@Target(ElementType.LOCAL_VARIABLE) //局部变量
    7、@Target(ElementType.ANNOTATION_TYPE) //注解
    8、@Target(ElementType.PACKAGE) //包
    注：可以指定多个位置
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
/* @Retention：描述注解的生命周期，表示在什么级别保存该注解的信息
     1、@Retention(RetentionPolicy.SOURCE) //注解仅存在于源码中，在class字节码文件中不包含
     2、@Retention(RetentionPolicy.CLASS) //默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得
     3、@Retention(RetentionPolicy.RUNTIME) //注解会在class字节码文件中存在，在运行时可以通过反射获取到
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    //资源名称
    String name() default "default_resource_name";

    /**
     * 限制访问次数
     */
    int limit() default 3;

    /**
     * 访问控制缓存类型
     */
    CacheType cacheType() default CacheType.SECONDS_TYPE;

    /**
     * 提醒信息
     */
    String tipMsg() default "当前资源无法访问，请稍后再试！";

    String startTime() default "";

    String endTime() default "";


    /**
     * 限制类型
     */
    LimitKeyTypeEnum limitKeyType() default LimitKeyTypeEnum.IPADDRACCOUNT;

    enum CacheType {
        SECONDS_TYPE, //秒级
        MINUTES_TYPE, //分钟级
        EXCLUSIVE_TYPE; //特殊级
    }

}
