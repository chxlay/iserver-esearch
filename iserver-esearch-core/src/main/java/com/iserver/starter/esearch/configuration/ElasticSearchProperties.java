package com.iserver.starter.esearch.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 可选的配置（Spring自动会配置的）
 * Spring-boot-aoutconfigura 版本在自带 ES 7.x 以上版本的无需配置此处,Spring 已经自动加载配置属性
 *
 * @author Alay
 * @date 2021-07-09 23:08
 */
@Getter
@Setter
@ConfigurationProperties("spring.elasticsearch.rest")
public class ElasticSearchProperties {

    /**
     * 集群中所哟地址
     */
    private List<String> uris = new ArrayList(Collections.singletonList("http://localhost:9200"));
    /**
     * 链接用户名
     */
    private String username;
    /**
     * 链接密码
     */
    private String password;
    /**
     * 链接超时时间
     */
    private int connectionTimeout = -1;
    /**
     * 读超时时长秒
     */
    private int readTimeout = 30;
}
