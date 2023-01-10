package com.iserver.starter.esearch.listener;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 更新数据异步监听器
 *
 * @author Alay
 * @date 2023-01-10 18:25
 */
public interface UpdateListener extends ActionListener<UpdateResponse> {


    /**
     * ES 默认异步更新数据执行监听器
     */
    class DefUpdateListener implements UpdateListener {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onResponse(UpdateResponse response) {
            logger.warn("index:{},异步更新数据成功,action:{},status:{}", response.getIndex(), response.getResult().name(), response.status().name());
        }

        @Override
        public void onFailure(Exception e) {
            logger.error("异步更新数据成功,case:{},msg:{}", e.getCause(), e.getMessage());
        }
    }
}
