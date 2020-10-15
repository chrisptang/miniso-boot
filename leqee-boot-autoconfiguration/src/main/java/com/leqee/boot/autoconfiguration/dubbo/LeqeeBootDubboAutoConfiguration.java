package com.leqee.boot.autoconfiguration.dubbo;

import org.apache.dubbo.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(
        name = {"com.leqee.boot.autoconfiguration.annotation.EnableLeqeeDubbo"}
)
@PropertySource("classpath:leqee-boot.properties")
public class LeqeeBootDubboAutoConfiguration {

    private static final Map<String, String> DUBBO_REGISTRY_URLS = new HashMap<>();

    private static final String DEFAULT_ZOOKEEPER_URL = "zookeeper://localhost:2181";

    static {
        DUBBO_REGISTRY_URLS.put("local", "zookeeper://localhost:2181");
        DUBBO_REGISTRY_URLS.put("dev", "zookeeper://172.22.15.41:2181");
        DUBBO_REGISTRY_URLS.put("test", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("staging", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("production", "zookeeper://10.0.16.134:2181");
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${leqee-boot.env:dev}")
    private String applicationEnv;

    @Bean
    @ConfigurationProperties(prefix = "dubbo.application")
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);

        return applicationConfig;
    }

    @Bean
    @ConfigurationProperties(prefix = "dubbo.registry")
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        if (DUBBO_REGISTRY_URLS.containsKey(applicationEnv)) {
            registryConfig.setAddress(DUBBO_REGISTRY_URLS.get(applicationEnv));
        } else {
            registryConfig.setAddress(DEFAULT_ZOOKEEPER_URL);
        }
        registryConfig.setId(applicationName + "-registry:" + registryConfig.getAddress());
        return registryConfig;
    }

    @Autowired
    private RegistryConfig registryConfig;

    @Bean
    @DependsOn("registryConfig")
    @ConfigurationProperties(prefix = "dubbo.config-center")
    public ConfigCenterConfig configCenterConfig() {
        ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();
        configCenterConfig.setAddress(registryConfig.getAddress());

        return configCenterConfig;
    }

    @Bean
    @DependsOn("registryConfig")
    @ConfigurationProperties(prefix = "dubbo.provider")
    public ProviderConfig providerConfig() {
        return new ProviderConfig();
    }

    @Bean
    @DependsOn("registryConfig")
    @ConfigurationProperties(prefix = "dubbo.consumer")
    public ConsumerConfig consumerConfig() {
        return new ConsumerConfig();
    }

    @Bean
    @DependsOn("registryConfig")
    @ConfigurationProperties(prefix = "dubbo.protocol")
    public ProtocolConfig protocolConfig() {
        return new ProtocolConfig();
    }
}
