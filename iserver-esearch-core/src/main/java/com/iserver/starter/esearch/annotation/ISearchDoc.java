package com.iserver.starter.esearch.annotation;

import java.lang.annotation.*;

/**
 * ElasticSearch实体类注解,标明实体类存储的索引及Type
 *
 * @author Alay
 * @date 2020-11-18 15:53
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ISearchDoc {

    /**
     * ES 索引 纯小写命名
     *
     * @return
     */
    String index() default "";

    /**
     * ES7以后默认_doc，即移除了type(小驼峰命名)
     *
     * @return
     */
    String type() default "_doc";

}