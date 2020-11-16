package com.leqee.ad.loghub;

import java.io.Serializable;
import java.util.Collection;

/**
 * 日志搜索
 */
public interface LogSearcher {

    /**
     * 搜索默认的log store里面的日志；
     *
     * @param query  请参考阿里云日志服务的query语法，举例：campaignId:123456 and adgroupId:7890
     * @param tClass 目标entity类
     * @param <T>
     * @return
     */
    <T extends Serializable> Collection<T> search(String query, Class<T> tClass);

    <T extends Serializable> Collection<T> search(String query, LogSearchPaging paging, Class<T> tClass);
}
