package com.leqee.boot.autoconfiguration.dubbo;

import com.leqee.boot.autoconfiguration.common.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.leqee.boot.autoconfiguration.NetworkUtil.pickAvailablePort;

@Configuration
@ConditionalOnClass(
        name = {"com.leqee.boot.autoconfiguration.annotation.EnableLeqeeDubbo"}
)
@Slf4j
@PropertySource("classpath:leqee-boot.properties")
public class LeqeeBootDubboAutoConfiguration {

    private static final Map<String, String> DUBBO_REGISTRY_URLS = new HashMap<>();

    private static final String DEFAULT_ZOOKEEPER_URL = "zookeeper://localhost:2181";

    static {
        DUBBO_REGISTRY_URLS.put("local", "zookeeper://localhost:2181");
        DUBBO_REGISTRY_URLS.put("dev", "zookeeper://172.22.15.41:2181");
        DUBBO_REGISTRY_URLS.put("fat", "zookeeper://10.0.16.134:2181");
        DUBBO_REGISTRY_URLS.put("prod", "zookeeper://10.0.16.140:2181");
    }

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${dubbo.registry.address:}")
    private String dubboRegistryAddress;

    @Value("${dubbo.registry.port:20887}")
    private int dubboPort;

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
        String env = EnvUtil.getEnv();
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setId(applicationName + "-dubbo-registry");

        if (StringUtils.isEmpty(dubboRegistryAddress)) {
            //set dubbo registry center address
            if (DUBBO_REGISTRY_URLS.containsKey(env)) {
                registryConfig.setAddress(DUBBO_REGISTRY_URLS.get(env));
            } else {
                registryConfig.setAddress(DEFAULT_ZOOKEEPER_URL);
            }
        } else {
            registryConfig.setAddress(RegistrySelector.selectPremierRegistryAddress(dubboRegistryAddress));
            System.setProperty("dubbo.registry.address", registryConfig.getAddress());
        }

        return registryConfig;
    }

    @Bean
    @DependsOn("registryConfig")
    @ConfigurationProperties(prefix = "dubbo.config-center")
    public ConfigCenterConfig configCenterConfig() {
        ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();
        configCenterConfig.setAddress(dubboRegistryAddress);
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
        ProtocolConfig protocolConfig = new ProtocolConfig();

        int dubboPortToUse = pickAvailablePort(dubboPort, "Dubbo");
        protocolConfig.setPort(dubboPortToUse);
        System.setProperty("dubbo.protocol.port", dubboPortToUse + "");

        return protocolConfig;
    }
}
