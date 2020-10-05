package com.leqee.boot.autoconfiguration.xxljob;

import java.io.Serializable;

public class XxlJobConfig implements Serializable {

    //### xxl-job admin address list, such as "http://address" or "http://address01,http://address02"
    private String adminAddresses;

    //            ### xxl-job, access token, could be empty
    private String accessToken;

    //            ### xxl-job executor appname, eg.xxl-job-executor-sample
    private String appName;
    //### xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null
    // XxlJobExecutor will pick the right IP and port;
//    private String address;
    //            ### xxl-job executor server-info
//    private Integer ip;
    private int port = 9999;//default to be 9999
    //            ### xxl-job executor log-path
//### /data/applogs/xxl-job/jobhandler
    private String logPath;
    //            ### xxl-job executor log-retention-days
    private int logRetentionDays = 30;

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    @Override
    public String toString() {
        return String.format("XxlJobConfig(adminAddress:%s, port:%d, appName:%s)", adminAddresses, port, appName);
    }
}
