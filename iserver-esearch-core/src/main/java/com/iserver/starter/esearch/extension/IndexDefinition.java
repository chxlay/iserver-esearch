package com.iserver.starter.esearch.extension;

/**
 * 定义描述 ES 索引的类
 *
 * @author Alay
 * @date 2021-08-05 00:45
 */
public class IndexDefinition {
    /**
     * 索引
     */
    private String index;
    /**
     * doc
     */
    private String type = "_doc";
    /**
     * Es 数据Id
     */
    private String id;


    public static IndexDefinition build() {
        return new IndexDefinition();
    }

    public String index() {
        return this.index;
    }

    public String type() {
        return this.type;
    }

    public String id() {
        return this.id;
    }


    public IndexDefinition index(String index) {
        this.index = index;
        return this;
    }

    public IndexDefinition type(String type) {
        this.type = type;
        return this;
    }

    public IndexDefinition id(String id) {
        this.id = id;
        return this;
    }

}
