package com.leqee.ad.loghub.autoconfiguration;

import com.leqee.ad.loghub.LogHubAgent;
import com.leqee.ad.loghub.context.LogHubBeanImportRegistrar;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnBean(LogHubBeanImportRegistrar.LogHubChecker.class)
@PropertySource("classpath:leqee-ad-loghub.properties")
@Import(LogHubAgent.class)
public class LeqeeAdLogHubAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "aliyun.loghub.leqeead")
    @ConditionalOnProperty("aliyun.loghub.leqeead.project")
    public LogHubConfig logHubConfig() {
        LogHubConfig logHubConfig = new LogHubConfig();
        String env = EnvUtil.getEnv();
        if ("dev".equalsIgnoreCase(env) || "local".equalsIgnoreCase(env)) {
            //dev和local环境使用外网
            logHubConfig.setEndPoint("cn-zhangjiakou.log.aliyuncs.com");
        } else {
            //其他环境使用内网
            logHubConfig.setEndPoint("cn-zhangjiakou-intranet.log.aliyuncs.com");
        }
        return logHubConfig;
    }
}
