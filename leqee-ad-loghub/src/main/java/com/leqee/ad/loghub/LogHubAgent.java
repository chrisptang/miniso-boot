package com.leqee.ad.loghub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.aliyun.log.producer.LogProducer;
import com.aliyun.openservices.aliyun.log.producer.Producer;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfig;
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

    @Override
    public <T extends Serializable> Collection<T> search(String query, Class<T> tClass) {
        return search(query, LogSearchPaging.defaultPaging(), tClass);
    }

    @Override
    public <T extends Serializable> Collection<T> search(String query, LogSearchPaging paging, Class<T> tClass) {
        return search(query, "", paging, tClass);
    }

    @Override
    public <T extends Serializable> Collection<T> search(String query, String topic, LogSearchPaging paging, Class<T> tClass) {
        if (null == topic) {
            topic = "";
        }
        if (null == paging) {
            paging = LogSearchPaging.defaultPaging();
        }
        try {
            GetLogsRequest request = new GetLogsRequest(
                    logHubConfig.getProject()
                    , logHubConfig.getLogStore()
                    , paging.getFrom()
                    , paging.getTo(), topic, query
                    , paging.getOffset(), paging.getPageSize(), paging.isEarliestFirst());
            GetLogsResponse response = LOG_HUB_CLIENT.get().GetLogs(request);
            log.info(JSON.toJSONString(response.GetAllHeaders()));
            ArrayList<QueriedLog> logs = response.GetLogs();
            return logs.stream().map(queriedLog -> {
                return JSON.parseObject(queriedLog.GetLogItem().ToJsonString(), tClass);
            }).collect(Collectors.toList());
        } catch (LogException e) {
            throw new RuntimeException("Unable to search log:" + query, e);
        }
    }

    @Override
    public int searchAndCount(String query, String topic, LogSearchPaging paging) {
        if (null == topic) {
            topic = "";
        }
        if (null == paging) {
            paging = LogSearchPaging.defaultPaging();
        }
        if (query == null || query.trim().length() <= 0) {
            throw new IllegalArgumentException("param query is empty:" + query);
        }
        if (query.contains("|")) {
            throw new IllegalArgumentException("param query should not contains '|':\t" + query);
        }
        query += "|select count(*) as cnt";

        try {
            GetLogsRequest request = new GetLogsRequest(
                    logHubConfig.getProject()
                    , logHubConfig.getLogStore()
                    , paging.getFrom()
                    , paging.getTo(), topic, query
                    , paging.getOffset(), paging.getPageSize(), paging.isEarliestFirst());
            GetLogsResponse response = LOG_HUB_CLIENT.get().GetLogs(request);
            log.info(JSON.toJSONString(response.GetAllHeaders()));
            ArrayList<QueriedLog> logs = response.GetLogs();
            if (CollectionUtils.isEmpty(logs)) {
                log.warn("query returned empty result:" + query);
                return 0;
            }
            JSONObject countLog = JSON.parseObject(logs.get(0).GetLogItem().ToJsonString());
            if (countLog.containsKey("cnt")) {
                return countLog.getIntValue("cnt");
            }
        } catch (LogException e) {
            throw new RuntimeException("Unable to search-and-count log:" + query, e);
        }

        return 0;
    }

    @Override
    public void log(Collection<? extends Serializable> logEntities) {
        log("", logEntities);
    }

    @Override
    public void log(String topic, Collection<? extends Serializable> logEntities) {
        if (CollectionUtils.isEmpty(logEntities)) {
            return;
        }
        try {
            MESSAGE_PRODUCER.get().send(logHubConfig.getProject(), logHubConfig.getLogStore()
                    , topic, "", logEntities.stream().map(LogHubAgent::buildLogItem).collect(Collectors.toList())
                    , result -> {
                        if (result == null) {
                            return;
                        }
                        if (result.isSuccessful()) {
                            log.info("LogHub success:" + result.toString());
                        } else {
                            log.warn("LogHub failed:" + result.toString());
                        }
                    });
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
        LogProducer producer = new LogProducer(producerConfig);
        ProjectConfig projectConfig = new ProjectConfig(
                logHubConfig.getProject()
                , logHubConfig.getEndPoint()
                , logHubConfig.getAccessKey()
                , logHubConfig.getAccessSecret());
        producer.putProjectConfig(projectConfig);

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
