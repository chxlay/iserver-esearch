package com.iserver.starter.esearch.serverice;

import com.iserver.starter.esearch.extension.ISearchModel;
import com.iserver.starter.esearch.extension.ISearchPage;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Collection;
import java.util.List;

/**
 * ElasticSearch 搜索常用方法封装
 *
 * @author Alay
 * @date 2022-02-12 09:21
 * @since 7.x 版本,不适合8.x 版本
 */
public interface ISearchService<T extends ISearchModel> {

    /**
     * 创建模板
     *
     * @param sourceStr
     * @return
     */
    Boolean putTemplate(String sourceStr);


    /**
     * 索引是否存在
     *
     * @param indexName
     * @return
     */
    Boolean existsIndex(String indexName);

    /**
     * 创建索引
     *
     * @param sourceStr
     * @return
     */
    Boolean createIndex(String sourceStr);

    /**
     * 创建索引
     *
     * @param index
     * @param sourceStr
     * @return
     */
    Boolean createIndex(String index, String sourceStr);

    /**
     * 移除索引
     *
     * @param index
     * @return
     */
    Boolean removeIndex(String index);

    /**
     * 添加数据保存到ES
     *
     * @param entity
     * @return
     */
    Boolean saveEntity(T entity);

    /**
     * 异步添加数据(异步)
     *
     * @param entity
     * @param listener
     */
    void saveEntityAsy(T entity, ActionListener listener);

    /**
     * 批量添加数据到ES
     *
     * @param entities
     * @return
     */
    Boolean saveBatch(Collection<T> entities);

    /**
     * 指定ID查询
     *
     * @param id
     * @return
     */
    T selectById(String id);

    /**
     * 指定ID集合查询
     *
     * @param ids
     * @return
     */
    List<T> listByIds(Collection<String> ids);

    /**
     * 根据Id 修改数据(同步)
     *
     * @param entity
     * @return
     */
    Boolean updateById(T entity);

    /**
     * 根据Id 修改同步数据(异步)
     *
     * @param entity
     * @param listener
     */
    void updateByIdAsy(T entity, ActionListener listener);

    /**
     * 指定Id删除
     *
     * @param id
     * @return
     */
    Boolean deleteById(String id);

    /**
     * 指定Id删除(异步)
     *
     * @param id
     * @param listener
     * @return
     */
    void deleteByIdAsy(String id, ActionListener listener);

    /**
     * 指定Id批量删除
     *
     * @param ids
     * @return
     */
    Boolean deleteByIds(Collection<String> ids);

    /**
     * 高级搜索
     *
     * @param searchRequest
     * @param options
     * @return
     */
    List<T> search(SearchSourceBuilder searchRequest, RequestOptions options);

    /**
     * 条件查询搜索
     *
     * @param page
     * @param searchSource
     * @return
     */
    ISearchPage<T> searchPage(ISearchPage<T> page, SearchSourceBuilder searchSource);

    /**
     * 多个索引下查询,多Type下查询
     *
     * @param page
     * @param searchSource
     * @param indices      多索引查询
     * @return
     */
    ISearchPage<T> searchPage(ISearchPage<T> page, SearchSourceBuilder searchSource, String... indices);

}
