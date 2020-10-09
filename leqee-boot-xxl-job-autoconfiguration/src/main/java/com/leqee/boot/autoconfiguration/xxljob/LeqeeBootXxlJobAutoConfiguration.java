package com.leqee.boot.autoconfiguration.xxljob;

import com.leqee.boot.autoconfiguration.common.LogPathUtil;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

@Configuration
@PropertySource(value = {"classpath:leqee-xxl-job.properties"})
@ConditionalOnClass(name = {"com.leqee.boot.autoconfiguration.xxljob.annotation.EnableXxlJob"})
public class LeqeeBootXxlJobAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(LeqeeBootXxlJobAutoConfiguration.class);

    @Value("${spring.application.name:unknown}")
    private String application;

    @Autowired
    private XxlJobConfig xxlJobConfig;

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "xxl.job.executor")
    public XxlJobConfig xxlJobConfig() {
        return new XxlJobConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobSpringExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAppname(application);
        xxlJobSpringExecutor.setAdminAddresses(xxlJobConfig.getAdminAddresses());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobConfig.getLogRetentionDays());
        xxlJobSpringExecutor.setPort(xxlJobConfig.getPort());
        xxlJobSpringExecutor.setAccessToken(xxlJobConfig.getAccessToken());

        // Log path, default to be /home/user-name/logs/application-name/xxl-job/
        if (StringUtils.isEmpty(xxlJobConfig.getLogPath())) {
            xxlJobSpringExecutor.setLogPath(LogPathUtil.ensureLogPath(application, "xxl-job"));
        } else {
            xxlJobSpringExecutor.setLogPath(xxlJobConfig.getLogPath());
        }

        logger.info("XXL-JOB configured as:" + xxlJobConfig);

        return xxlJobSpringExecutor;
    }
}
