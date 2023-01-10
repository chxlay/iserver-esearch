package com.iserver.starter.esearch.configuration;

import com.iserver.starter.esearch.listener.DeleteListener;
import com.iserver.starter.esearch.listener.IndexListener;
import com.iserver.starter.esearch.listener.UpdateListener;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 可选的配置（Spring自动会配置的）
 * Spring-boot-aoutconfigura 版本在自带 ES 7.x 以上版本的无需配置此处,Spring 已经自动加载
 *
 * @author Alay
 * @date 2021-07-09 23:07
 */
@EnableConfigurationProperties(value = {ElasticSearchProperties.class})
public class ESearchAutoConfiguration {

    @Autowired
    private ElasticSearchProperties searchProperties;

    /**
     * @param restClientBuilder
     * @return
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-getting-started-initialization.html">官方文档</a>
     */
    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient elasticsearchRestHighLevelClient(RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }


    @Bean
    @Order
    @ConditionalOnMissingBean
    public IndexListener indexListener() {
        return new IndexListener.DefIndexListener();
    }

    @Bean
    @Order
    @ConditionalOnMissingBean
    public UpdateListener updateListener() {
        return new UpdateListener.DefUpdateListener();
    }

    @Bean
    @Order
    @ConditionalOnMissingBean
    public DeleteListener deleteListener() {
        return new DeleteListener.DefDeleteListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = searchProperties.getUris().stream().map(this::createHttpHost).toArray((x$0) ->
                new HttpHost[x$0]
        );

        // 集群链接地址构建
        RestClientBuilder builder = RestClient.builder(hosts);

        // 配置用户名和密码
        final BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(
                        // 用户名
                        searchProperties.getUsername(),
                        // 密码
                        searchProperties.getPassword()
                )
        );

        builder
                //设置超时
                .setRequestConfigCallback((RequestConfig.Builder requestConfigBuilder) -> {
                    requestConfigBuilder.setConnectTimeout(searchProperties.getConnectionTimeout());
                    requestConfigBuilder.setSocketTimeout(-1);
                    requestConfigBuilder.setConnectionRequestTimeout(-1);
                    return requestConfigBuilder;
                })
                // 配置客户端密码
                .setHttpClientConfigCallback((HttpAsyncClientBuilder httpClientBuilder) -> {
                    httpClientBuilder
                            .disableAuthCaching()
                            .setDefaultCredentialsProvider(provider);
                    return httpClientBuilder;
                });

        return builder;
    }

    private HttpHost createHttpHost(String uri) {
        try {
            return this.createHttpHost(URI.create(uri));
        } catch (IllegalArgumentException var3) {
            return HttpHost.create(uri);
        }
    }


    private HttpHost createHttpHost(URI uri) {
        if (!StringUtils.hasLength(uri.getUserInfo())) {
            return HttpHost.create(uri.toString());
        } else {
            try {
                return HttpHost.create((new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment())).toString());
            } catch (URISyntaxException exception) {
                throw new IllegalStateException(exception);
            }
        }
    }

}
