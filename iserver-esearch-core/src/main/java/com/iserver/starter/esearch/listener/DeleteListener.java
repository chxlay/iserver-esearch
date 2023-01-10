package com.iserver.starter.esearch.listener;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 删除数据异步监听器
 *
 * @author Alay
 * @date 2023-01-10 18:20
 */
public interface DeleteListener extends ActionListener<DeleteResponse> {

    /**
     * ES 默认异步删除数据执行监听器
     */
    class DefDeleteListener implements DeleteListener {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onResponse(DeleteResponse response) {
            logger.warn("index:{},异步删除数据成功,action:{},status:{}", response.getIndex(), response.getResult().name(), response.status().name());
        }

        @Override
        public void onFailure(Exception e) {
            logger.error("异步删除数据成功,case:{},msg:{}", e.getCause(), e.getMessage());
        }
    }

}
