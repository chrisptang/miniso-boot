package com.leqee.boot.autoconfiguration.apollo;

import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自动装配Apollo的远程配置；不同的环境不同的server地址；
 */
@Configuration
@ConditionalOnBean({ApolloBeanImportRegistrar.EnableApolloChecker.class})
@EnableApolloConfig({ConfigConsts.NAMESPACE_APPLICATION, "leqee-ad.ad-common-config"})
public class LeqeeBootApolloAutoConfiguration implements SmartInitializingSingleton {
    private static final Logger logger = LoggerFactory.getLogger(LeqeeBootApolloAutoConfiguration.class);

    private static final Map<String, String> APOLLO_CONFIGURATION_SERVER = new HashMap<>();

    static {
        APOLLO_CONFIGURATION_SERVER.put("dev", "http://172.22.15.41:30004");
        APOLLO_CONFIGURATION_SERVER.put("local", "http://127.0.0.1:30004");
        APOLLO_CONFIGURATION_SERVER.put("fat", "http://47.92.192.33:30004");
        APOLLO_CONFIGURATION_SERVER.put("prod", "http://10.0.16.134:30004");

        String env = EnvUtil.getEnv();
        if (!APOLLO_CONFIGURATION_SERVER.containsKey(env)) {
            //fullback to dev;
            env = "dev";
        }

        System.setProperty("apollo.configService", APOLLO_CONFIGURATION_SERVER.get(env));
        System.setProperty("apollo.meta", APOLLO_CONFIGURATION_SERVER.get(env));
        System.setProperty("apollo.bootstrap.eagerLoad.enabled", "true");
    }

    @Override
    public void afterSingletonsInstantiated() {
        Set<String> names = ConfigService.getAppConfig().getPropertyNames();
        logger.info("=====\nApollo configuration keys:\n" + names.stream().collect(Collectors.joining(",")));
        for (String key : names) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Apollo key:%s, value:%s", key, ConfigService.getAppConfig().getProperty(key, "")));
            }
        }
    }
}
