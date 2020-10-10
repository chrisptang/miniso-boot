package com.leqee.boot.autoconfiguration.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(
        name = {"org.apache.dubbo.config.spring.context.annotation.EnableDubbo"}
)
@PropertySource("classpath:leqee-boot.properties")
public class LeqeeBootDubboAutoConfiguration {

    private static final Map<String, String> DUBBO_REGISTRY_URLS = new HashMap<>();

    private static final String DEFAULT_ZOOKEEPER_URL = "zookeeper://localhost:2181";

    static {
        DUBBO_REGISTRY_URLS.put("dev", "zookeeper://172.22.201.64:2181");
        DUBBO_REGISTRY_URLS.put("test", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("staging", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("production", "zookeeper://10.0.16.134:2181");
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${leqee-boot.env:dev}")
    private String applicationEnv;

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.application")
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName + "-from-auto-configuration");

        return applicationConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.registry")
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        if (DUBBO_REGISTRY_URLS.containsKey(applicationEnv)) {
            registryConfig.setAddress(DUBBO_REGISTRY_URLS.get(applicationEnv));
        } else {
            registryConfig.setAddress(DEFAULT_ZOOKEEPER_URL);
        }
        return registryConfig;
    }
}
