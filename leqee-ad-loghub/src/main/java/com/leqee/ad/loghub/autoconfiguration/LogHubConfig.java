package com.leqee.ad.loghub.autoconfiguration;

import lombok.Data;

@Data
public class LogHubConfig {
    private String project;

    private String endPoint;

    private String accessKey;

    private String accessSecret;

    private String logStore;
}
