package com.iserver.starter.esearch.listener;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 创建数据默认监听器
 *
 * @author Alay
 * @date 2021-04-20 19:49
 */
public interface IndexListener extends ActionListener<IndexResponse> {

    /**
     * ES 默认异步添加数据执行监听器
     */
    class DefIndexListener implements IndexListener {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onResponse(IndexResponse response) {
            logger.warn("index:{},异步新增数据成功,action:{},status:{}", response.getIndex(), response.getResult().name(), response.status().name());
        }

        @Override
        public void onFailure(Exception e) {
            logger.error("异步新增数据成功,case:{},msg:{}", e.getCause(), e.getMessage());
        }
    }
}