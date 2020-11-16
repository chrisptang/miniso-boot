package com.leqee.ad.loghub;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LogSearchPaging {
    /**
     * 查询日志的起始日期,时间戳，秒单位
     * 默认值为2天前；
     */
    private int from;
    /**
     * 查询日志的截止日志,时间戳，秒单位
     * 默认值为当日当时；
     */
    private int to;
    /**
     * 分页的offset；
     * 默认值为0
     */
    private int offset;
    /**
     * 每次获取多少条
     * 默认值为20
     */
    private int pageSize;

    /**
     * 最早入栈的日志优先(FIFO)
     * 默认为最新日志优先(LIFO),即：时间倒序排序
     */
    private boolean earliestFirst;

    public LogSearchPaging() {
        final int currentTimeInSecond = (int) System.currentTimeMillis() / 1000;
        this.setPageSize(20)
                .setOffset(0)
                .setEarliestFirst(false)
                .setTo(currentTimeInSecond)
                .setFrom(currentTimeInSecond - 2 * 3600 * 24);
    }

    public static LogSearchPaging defaultPaging() {
        final int currentTimeInSecond = (int) System.currentTimeMillis() / 1000;
        return new LogSearchPaging()
                .setPageSize(20)
                .setOffset(0)
                .setEarliestFirst(false)
                //默认取2天以内的数据；
                .setFrom(currentTimeInSecond - 2 * 3600 * 24)
                .setTo(currentTimeInSecond);
    }
}
