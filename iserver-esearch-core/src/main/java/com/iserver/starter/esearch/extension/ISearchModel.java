package com.iserver.starter.esearch.extension;

import java.io.Serializable;

/**
 * 规范 Es实体类
 *
 * @author Alay
 * @date 2022-02-12 09:23
 */
public interface ISearchModel<T extends ISearchModel> extends Serializable {

    /**
     * ES7以后默认_doc，即移除了type(小驼峰命名)
     */
    String TYPE = "_doc";

    /**
     * ES 索引 纯小写命名
     *
     * @return
     */
    default String index() {
        return null;
    }

    /**
     * 获取Type,ES7以后默认_doc，即移除了type(小驼峰命名)
     *
     * @return
     */
    default String type() {
        return TYPE;
    }


    /**
     * 获得ES主键ID
     *
     * @return
     */
    String id();

    /**
     * index 的Setter 函数
     *
     * @param index
     * @return
     */
    default T index(String index) {
        return (T) this;
    }

}
