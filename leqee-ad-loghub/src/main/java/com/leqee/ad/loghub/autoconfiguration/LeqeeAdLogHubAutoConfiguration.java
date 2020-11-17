package com.leqee.ad.loghub.autoconfiguration;

import com.leqee.ad.loghub.LogHubAgent;
import com.leqee.ad.loghub.context.LogHubBeanImportRegistrar;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import com.leqee.boot.autoconfiguration.common.SupportedEnv;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${aliyun.loghub.leqeead.log-store}")
    private String logStore;

    @Bean
    @ConfigurationProperties(prefix = "aliyun.loghub.leqeead")
    @ConditionalOnProperty({"aliyun.loghub.leqeead.project", "aliyun.loghub.leqeead.log-store"})
    public LogHubConfig logHubConfig() {
        LogHubConfig logHubConfig = new LogHubConfig();
        SupportedEnv env = EnvUtil.getSupportedEnv();
        if (SupportedEnv.Dev.equals(env) || SupportedEnv.Local.equals(env)) {
            //dev和local环境使用外网
            logHubConfig.setEndPoint("cn-zhangjiakou.log.aliyuncs.com");
        } else {
            //其他环境使用内网
            logHubConfig.setEndPoint("cn-zhangjiakou-intranet.log.aliyuncs.com");
        }
        if (SupportedEnv.Prod.equals(env)) {
            logHubConfig.setLogStorage(logStore);
        } else {
            //非正式环境使用测试log store；
            logHubConfig.setLogStorage(logStore + "-test");
        }
        return logHubConfig;
    }
}
