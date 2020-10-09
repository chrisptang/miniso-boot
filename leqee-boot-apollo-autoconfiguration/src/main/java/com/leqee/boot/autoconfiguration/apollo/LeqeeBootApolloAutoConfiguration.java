package com.leqee.boot.autoconfiguration.apollo;

import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自动装配Apollo的远程配置；不同的环境不同的server地址；
 */
@Configuration
@EnableApolloConfig
public class LeqeeBootApolloAutoConfiguration implements SmartInitializingSingleton {
    private static final Logger logger = LoggerFactory.getLogger(LeqeeBootApolloAutoConfiguration.class);

    private static final Map<String, String> APOLLO_CONFIGURATION_SERVER = new HashMap<>();

    static {
        APOLLO_CONFIGURATION_SERVER.put("dev", "http://172.22.201.64:30004");
        APOLLO_CONFIGURATION_SERVER.put("test", "http://10.0.16.134:30004");
        APOLLO_CONFIGURATION_SERVER.put("staging", "http://10.0.16.134:30004");
        APOLLO_CONFIGURATION_SERVER.put("production", "http://10.0.16.134:30004");

        String env = "dev";//默认
        env = System.getProperty("leqee-boot.env", env);//通过leqee-boot.env覆盖
        env = System.getProperty("env", env);//系统的env属性为最高优先级；

        System.setProperty("apollo.configService", APOLLO_CONFIGURATION_SERVER.get(env));
        // apollo.meta和apollo.configService 一般来说是同一个JVM实例，或者同一个集群；
        System.setProperty("apollo.meta", APOLLO_CONFIGURATION_SERVER.get(env));
        if (!System.getProperties().containsKey("env")) {
            System.setProperty("env", env);
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        Set<String> names = ConfigService.getAppConfig().getPropertyNames();
        logger.info("Apollo configurations:\n" + names.stream().collect(Collectors.joining(",")));
        for (String key : names) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Apollo key:%s, value:%s", key, ConfigService.getAppConfig().getProperty(key, "")));
            }
        }
    }
}
