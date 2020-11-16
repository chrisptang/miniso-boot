package com.leqee.ad.loghub;

import java.io.Serializable;
import java.util.Collection;

/**
 * 日志存储操作
 */
public interface LogStorage {

    /**
     * 往默认logstore里面写入一批日志，默认是JSON序列化；
     *
     * @param logEntities
     */
    void log(Collection<? extends Serializable> logEntities);
}
