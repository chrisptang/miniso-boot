package com.miniso.boot.autoconfiguration.xxljob;

import com.miniso.boot.autoconfiguration.common.LogPathUtil;
import com.miniso.boot.autoconfiguration.xxljob.beanregistry.XxlJobBeanRegistrar;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import static com.miniso.boot.autoconfiguration.NetworkUtil.pickAvailablePort;

@Configuration
@PropertySource(value = {"classpath:miniso-xxl-job.properties"})
@ConditionalOnBean({XxlJobBeanRegistrar.EnableXxlJobChecker.class})
public class MinisoBootXxlJobAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MinisoBootXxlJobAutoConfiguration.class);

    private static final String DEFAULT_ADMIN_ADDRESS = "http://localhost:30003/xxl-job-admin";

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
        if (StringUtils.isEmpty(xxlJobConfig.getAdminAddresses())) {
            logger.warn("\n========\nconfiguration key:xxl.job.executor.adminAddress is not found, using default address:" + DEFAULT_ADMIN_ADDRESS);
            xxlJobSpringExecutor.setAdminAddresses(DEFAULT_ADMIN_ADDRESS);
        } else {
            xxlJobSpringExecutor.setAdminAddresses(xxlJobConfig.getAdminAddresses());
        }
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobConfig.getLogRetentionDays());

        int xxlPortToUse = pickAvailablePort(xxlJobConfig.getPort(), "XXL-JOB");
        xxlJobSpringExecutor.setPort(xxlPortToUse);
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

    @Bean
    @ConditionalOnMissingBean
    public XxlJobCatIntegration xxlJobCatIntegration() {
        return new XxlJobCatIntegration();
    }
}
