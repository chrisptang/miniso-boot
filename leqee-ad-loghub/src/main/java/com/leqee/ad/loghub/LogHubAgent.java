package com.leqee.ad.loghub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.Producer;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetLogsResponse;
import com.leqee.ad.loghub.autoconfiguration.LogHubConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class LogHubAgent implements InitializingBean, DisposableBean, LogSearcher, LogStorage {

    @Autowired
    private LogHubConfig logHubConfig;

    @Bean
    public LogSearcher logSearcher() {
        return this;
    }

    @Bean
    public LogStorage logStorage() {
        return this;
    }

    @Override
    public <T extends Serializable> Collection<T> search(String query, Class<T> tClass) {
        LogSearchPaging paging = new LogSearchPaging();
        paging.setFrom((int) (System.currentTimeMillis() / 1000 - 3600 * 48));
        paging.setOffset(0);
        paging.setPageSize(100);
        paging.setTo((int) (System.currentTimeMillis() / 1000));
        return search(query, paging, tClass);
    }

    @Override
    public <T extends Serializable> Collection<T> search(String query, LogSearchPaging paging, Class<T> tClass) {
        try {
            GetLogsRequest request = new GetLogsRequest(
                    logHubConfig.getProject()
                    , logHubConfig.getLogStore()
                    , paging.getFrom()
                    , paging.getTo(), "", query
                    , paging.getOffset(), paging.getPageSize(), paging.isEarliestFirst());
            GetLogsResponse response = LOG_HUB_CLIENT.get().GetLogs(request);
            log.info(JSON.toJSONString(response.GetAllHeaders()));
            ArrayList<QueriedLog> logs = response.GetLogs();
            return logs.stream().map(queriedLog -> {
                return JSON.parseObject(queriedLog.GetLogItem().ToJsonString(), tClass);
            }).collect(Collectors.toList());
        } catch (LogException e) {
            throw new RuntimeException("Unable to search log:", e);
        }
    }

    @Override
    public void log(Collection<? extends Serializable> logEntities) {
        if (CollectionUtils.isEmpty(logEntities)) {
            return;
        }
        try {
            MESSAGE_PRODUCER.get().send(logHubConfig.getProject(), logHubConfig.getLogStore()
                    , logEntities.stream().map(LogHubAgent::buildLogItem).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to log:", e);
        }
    }

    private static final AtomicReference<Producer> MESSAGE_PRODUCER =
            new AtomicReference<>();

    private static final AtomicReference<Client> LOG_HUB_CLIENT = new AtomicReference<>();


    @Override
    public void destroy() throws Exception {
        MESSAGE_PRODUCER.get().close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ProducerConfig producerConfig = new ProducerConfig();
        producerConfig.setRetries(3);
        producerConfig.setMaxRetryBackoffMs(100L);
        Producer producer = new LogProducer(producerConfig);
        MESSAGE_PRODUCER.compareAndSet(null, producer);

        Client client = new Client(logHubConfig.getEndPoint(), logHubConfig.getAccessKey(), logHubConfig.getAccessSecret());
        log.info("LogHub is configured as:\n" + JSON.toJSONString(logHubConfig));
        LOG_HUB_CLIENT.compareAndSet(null, client);
    }

    private static LogItem buildLogItem(Serializable operationLog) {
        LogItem logItem = new LogItem();
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(operationLog));
        jsonObject.keySet().stream().forEachOrdered(key -> {
            logItem.PushBack(key, String.valueOf(jsonObject.get(key)));
        });

        return logItem;
    }
}
