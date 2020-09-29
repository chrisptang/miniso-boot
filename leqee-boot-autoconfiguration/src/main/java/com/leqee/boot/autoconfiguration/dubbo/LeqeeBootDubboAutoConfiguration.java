package com.leqee.boot.autoconfiguration.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(
        name = {"org.apache.dubbo.config.spring.context.annotation.EnableDubbo"}
)
@ConfigurationProperties
public class LeqeeBootDubboAutoConfiguration {

    private static final String APPLICATION_NAME_PROPERTY = "spring.application.name";

    private static final String APPLICATION_ENV_PROPERTY = "spring.application.env";

    private static final Map<String, String> DUBBO_REGISTRY_URLS = new HashMap<>();

    private static final String DEFAULT_ZOOKEEPER_URL = "zookeeper://localhost:2181";

    static {
        DUBBO_REGISTRY_URLS.put("dev", "zookeeper://localhost:2181");
        DUBBO_REGISTRY_URLS.put("test", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("staging", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("production", "zookeeper://10.0.16.134:2181");
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        String name = System.getProperty(APPLICATION_NAME_PROPERTY);
        applicationConfig.setName(name);

        return applicationConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        String env = System.getProperty(APPLICATION_ENV_PROPERTY);
        if (DUBBO_REGISTRY_URLS.containsKey(env)) {
            registryConfig.setAddress(DUBBO_REGISTRY_URLS.get(env));
        } else {
            registryConfig.setAddress(DEFAULT_ZOOKEEPER_URL);
        }
        return registryConfig;
    }
}
