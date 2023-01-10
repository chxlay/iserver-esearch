package com.iserver.starter.esearch.serverice.impl;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iserver.starter.esearch.annotation.ISearchDoc;
import com.iserver.starter.esearch.exeption.ESearchException;
import com.iserver.starter.esearch.extension.ISearchModel;
import com.iserver.starter.esearch.extension.ISearchPage;
import com.iserver.starter.esearch.extension.IndexDefinition;
import com.iserver.starter.esearch.serilization.LocalDateSerialization;
import com.iserver.starter.esearch.serilization.LocalDateTimeSerialization;
import com.iserver.starter.esearch.serverice.ISearchService;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alay
 * @date 2022-02-12 09:28
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-supported-apis.html">官方文档</a>
 * @since 7.x 版本,不适合8.x 版本
 */
public abstract class ISearchServiceImpl<T extends ISearchModel> implements ISearchService<T> {

    @Autowired
    protected RestHighLevelClient esClient;
    protected transient Log log = LogFactory.getLog(this.getClass());
    protected static Gson GSON;

    static {
        GSON = new GsonBuilder()
                // 注册自定义金额分的系列化规则
                //.registerTypeAdapter(MoneyPenny.class, new MoneyPennySerialization())
                // 时间系列化规则
                .registerTypeAdapter(LocalDate.class, new LocalDateSerialization())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerialization())
                .create();
    }


    /**
     * 文档：
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-put-template.html
     *
     * @param sourceStr
     * @return
     */
    @Override
    public Boolean putTemplate(String sourceStr) {
        PutIndexTemplateRequest templateRequest = new PutIndexTemplateRequest("demo*");
        templateRequest.source(sourceStr, XContentType.JSON);
        try {
            AcknowledgedResponse response = esClient.indices().putTemplate(templateRequest, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }


    @Override
    public Boolean existsIndex(String indexName) {
        if (StrUtil.isBlank(indexName)) {
            // 实现类泛型中的类对象
            Class<T> clazz = this.entityClass();
            // 从实现类泛型中去实体类的注解中的 index 值
            indexName = this.parseEsAnno(clazz).index();
        }
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        try {
            boolean exists = esClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
            return exists;
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 文档:https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-create-index.html
     *
     * @param sourceStr
     * @return
     */
    @Override
    public Boolean createIndex(String sourceStr) {
        // 实现类泛型中的类对象
        Class<T> clazz = this.entityClass();
        IndexDefinition indexDefinition = this.parseEsAnno(clazz);
        return this.createIndex(indexDefinition.index(), sourceStr);
    }

    @Override
    public Boolean createIndex(String index, String sourceStr) {
        CreateIndexRequest indexRequest = new CreateIndexRequest(index);
        indexRequest.source(sourceStr, XContentType.JSON);
        try {
            CreateIndexResponse indexResponse = esClient.indices().create(indexRequest, RequestOptions.DEFAULT);
            return indexResponse.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean removeIndex(String index) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse delete = esClient.indices().delete(request, RequestOptions.DEFAULT);
            return delete.isAcknowledged();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean saveEntity(T entity) {
        Class<? extends ISearchModel> clazz = entity.getClass();
        IndexDefinition IndexDefinition = this.parseEsAnno(clazz);
        // 解析实体类映射的 Index 信息
        IndexRequest indexRequest = new IndexRequest(IndexDefinition.index());
        indexRequest.id(entity.id());
        String jsonStr = GSON.toJson(entity);
        indexRequest.source(jsonStr, XContentType.JSON);
        try {
            IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
            int status = indexResponse.status().getStatus();
            return status == RestStatus.CREATED.getStatus() || status == RestStatus.OK.getStatus();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void saveEntityAsy(T entity, ActionListener listener) {
        Class<? extends ISearchModel> clazz = entity.getClass();
        IndexDefinition indexDefinition = this.parseEsAnno(clazz);
        // 解析实体类映射的 Index 信息
        IndexRequest indexRequest = new IndexRequest(indexDefinition.index());
        indexRequest.id(entity.id());
        String jsonStr = GSON.toJson(entity);
        indexRequest.source(jsonStr, XContentType.JSON);
        esClient.indexAsync(indexRequest, RequestOptions.DEFAULT, listener);
    }

    @Override
    public Boolean saveBatch(Collection<T> entities) {
        // 获取Service接口中泛型的实体类型
        Class<T> clazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(clazz);
        BulkRequest bulkRequest = new BulkRequest(index.index());
        // 添加批量实体类请求
        for (T entity : entities) {
            IndexRequest request = new IndexRequest();
            request.id(entity.id());
            request.source(GSON.toJson(entity), XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            BulkResponse bulkResponse = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = bulkResponse.status().getStatus();
            return status == RestStatus.OK.getStatus();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public T selectById(String id) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(entityClazz);
        GetRequest getRequest = new GetRequest(index.index());
        getRequest.id(id);
        try {
            GetResponse getResponse = esClient.get(getRequest, RequestOptions.DEFAULT);
            T entity = null;
            if (getResponse.isExists()) {
                String sourceJsonStr = getResponse.getSourceAsString();
                entity = GSON.fromJson(sourceJsonStr, entityClazz);
            }
            return entity;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<T> listByIds(Collection<String> ids) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(entityClazz);

        MultiGetRequest getRequest = new MultiGetRequest();
        for (String id : ids) {
            MultiGetRequest.Item item = new MultiGetRequest.Item(index.index(), id);
            getRequest.add(item);
        }
        try {
            MultiGetResponse multiGetResponse = esClient.mget(getRequest, RequestOptions.DEFAULT);
            List<T> entities = Arrays.stream(multiGetResponse.getResponses())
                    .map(res -> GSON.fromJson(res.getResponse().getSourceAsString(), entityClazz))
                    .collect(Collectors.toList());
            return entities;
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ArrayList<>(0);
        }
    }

    @Override
    public Boolean updateById(T entity) {
        Class<? extends ISearchModel> clazz = entity.getClass();
        IndexDefinition indexDefinition = this.parseEsAnno(clazz);
        UpdateRequest updateRequest = new UpdateRequest(indexDefinition.index(), entity.id());
        String jsonStr = GSON.toJson(entity);
        updateRequest.doc(jsonStr, XContentType.JSON);
        try {
            UpdateResponse updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);
            int status = updateResponse.status().getStatus();
            return status == RestStatus.OK.getStatus();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void updateByIdAsy(T entity, ActionListener listener) {
        Class<? extends ISearchModel> clazz = entity.getClass();
        IndexDefinition indexDefinition = this.parseEsAnno(clazz);
        UpdateRequest updateRequest = new UpdateRequest(indexDefinition.index(), entity.id());
        String jsonStr = GSON.toJson(entity);
        updateRequest.doc(jsonStr, XContentType.JSON);
        esClient.updateAsync(updateRequest, RequestOptions.DEFAULT, listener);
    }

    @Override
    public Boolean deleteById(String id) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(entityClazz);

        DeleteRequest deleteRequest = new DeleteRequest(index.index());
        deleteRequest.id(id);
        try {
            DeleteResponse response = esClient.delete(deleteRequest, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            return status == RestStatus.OK.getStatus();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteByIdAsy(String id, ActionListener listener) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(entityClazz);

        DeleteRequest deleteRequest = new DeleteRequest(index.index());
        deleteRequest.id(id);
        esClient.deleteAsync(deleteRequest, RequestOptions.DEFAULT, listener);
    }

    @Override
    public Boolean deleteByIds(Collection<String> ids) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(entityClazz);

        BulkRequest bulkRequest = new BulkRequest(index.index());
        for (String id : ids) {
            DeleteRequest deleteRequest = new DeleteRequest().id(id);
            bulkRequest.add(deleteRequest);
        }
        try {
            BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            return status == RestStatus.OK.getStatus();
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }


    @Override
    public List<T> search(SearchSourceBuilder searchSource, RequestOptions options) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        SearchRequest searchRequest = this.searchBuilder();
        searchRequest.source(searchSource);
        try {
            SearchResponse search = esClient.search(searchRequest, options);
            SearchHits hits = search.getHits();
            List<T> entities = Arrays.stream(hits.getHits())
                    .map(hit -> GSON.fromJson(hit.getSourceAsString(), entityClazz))
                    .collect(Collectors.toList());
            return entities;
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ArrayList<>(0);
        }
    }


    @Override
    public ISearchPage<T> searchPage(ISearchPage<T> page, SearchSourceBuilder searchSource) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        SearchRequest searchRequest = this.searchBuilder();
        searchRequest.source(searchSource);
        return this.page(page, searchRequest, entityClazz);
    }

    @Override
    public ISearchPage<T> searchPage(ISearchPage<T> page, SearchSourceBuilder searchSource, String... indices) {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        SearchRequest searchRequest = this.searchBuilder(indices);
        searchRequest.source(searchSource);
        return this.page(page, searchRequest, entityClazz);
    }

    protected ISearchPage<T> page(ISearchPage<T> page, SearchRequest searchRequest, Class<T> entityClazz) {
        try {
            SearchResponse search = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = search.getHits();
            TotalHits totalHits = hits.getTotalHits();
            long total = totalHits.value;
            List<T> entities = Arrays.stream(hits.getHits()).map(hit -> {
                T entity = GSON.fromJson(hit.getSourceAsString(), entityClazz);
                entity.index(hit.getIndex());
                return entity;
            }).collect(Collectors.toList());
            page.total(total).records(entities).size(entities.size());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return page;
    }

    SearchRequest searchBuilder(String... indices) {
        SearchRequest searchRequest = new SearchRequest(indices);
        return searchRequest;
    }


    SearchRequest searchBuilder() {
        // 获取Service接口中泛型的实体类型
        Class<T> entityClazz = this.entityClass();
        // 解析索引信息
        IndexDefinition index = this.parseEsAnno(entityClazz);
        return this.searchBuilder(index.index());
    }

    /**
     * 抽取距离查询的封装语句(坐标对象字段和规定的一致才能使用此方法)
     *
     * @param search
     * @param bool
     * @param lat
     * @param lon
     * @param distance
     */
    protected void buildGeo(SearchSourceBuilder search, BoolQueryBuilder bool, Double lat, Double lon, Long distance) {
        // 按距离范围筛选
        GeoDistanceQueryBuilder locationQuery = new GeoDistanceQueryBuilder("location");
        locationQuery.point(lat, lon);
        locationQuery.distance(distance, DistanceUnit.METERS);
        bool.filter(locationQuery);

        // 按距离排序
        GeoDistanceSortBuilder locationSort = new GeoDistanceSortBuilder("location", lat, lon);
        locationSort.unit(DistanceUnit.METERS);
        locationSort.order(SortOrder.ASC);
        search.sort(locationSort);
    }

    /**
     * 解析实体类映射ES的库表
     *
     * @param clazz ES实体bean的类信息
     * @return
     */
    @SneakyThrows
    protected IndexDefinition parseEsAnno(Class<? extends ISearchModel> clazz) {
        boolean hasAnno = clazz.isAnnotationPresent(ISearchDoc.class);
        if (!hasAnno) {
            // ES 实体 Bean 类没有 ISearchDocument 注解,抛出异常告知调用者
            throw new ESearchException("没有加入实体类注解 @ISearchDoc");
        }
        ISearchDoc esAnno = clazz.getAnnotation(ISearchDoc.class);
        String index = esAnno.index();
        if (Objects.equals(index, "")) {
            // 纯小写
            index = clazz.getSimpleName().toLowerCase();
        }
        String type = esAnno.type();
        if (Objects.equals(type, "")) {
            // 转小驼峰
            type = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
        }
        IndexDefinition indexModel = IndexDefinition.build().index(index).type(type);
        return indexModel;
    }

    /**
     * 获取调用方法实现类中泛型的具体类对象
     *
     * @return
     */
    protected Class<T> entityClass() {
        // 当前调用方法的 Impl实现类的父类的类型
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 当前调用方法的 Impl实现类的泛型的类型,实现类必须带泛型,否则报错
        Type[] type = superclass.getActualTypeArguments();
        // 泛型中第一个参数
        Class clazz = (Class) type[0];
        return clazz;
    }

}
