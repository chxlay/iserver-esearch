package com.iserver.starter.esearch.extension;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索分页对象
 *
 * @author Alay
 * @date 2022-02-12 09:38
 */
public class ISearchPage<T> implements Serializable {
    private static final long serialVersionUID = 8545996863226528798L;

    /**
     * 数据总数
     */
    @Getter
    @Setter
    private long total;
    /**
     * 当前页码
     */
    @Getter
    @Setter
    private int current = 1;
    /**
     * Search 查询起始为止
     */
    private int from;
    /**
     * 单次查询数据条数大小
     */
    @Getter
    @Setter
    private int size = 60;
    /**
     * 查询返回结果记录
     */
    @Getter
    @Setter
    private List<T> records;

    public ISearchPage total(long total) {
        this.total = total;
        return this;
    }

    public ISearchPage records(List<T> records) {
        this.records = records;
        return this;
    }

    public ISearchPage size(int size) {
        this.size = size;
        return this;
    }

    public int size() {
        return size;
    }

    public int from() {
        // 起始页 = 当前页码 -1 * 页码大小
        this.from = (this.current - 1) * size;
        return this.from;
    }

}
