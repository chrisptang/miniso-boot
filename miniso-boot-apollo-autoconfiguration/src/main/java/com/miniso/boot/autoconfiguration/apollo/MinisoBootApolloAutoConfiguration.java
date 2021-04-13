package com.miniso.boot.autoconfiguration.apollo;

import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.miniso.boot.autoconfiguration.common.DefaultConstants;
import com.miniso.boot.autoconfiguration.common.EnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.miniso.boot.autoconfiguration.common.DefaultConstants.TEST_HOST;

/**
 * 自动装配Apollo的远程配置；不同的环境不同的server地址；
 */
@Configuration
@ConditionalOnBean({ApolloBeanImportRegistrar.EnableApolloChecker.class})
@EnableApolloConfig({ConfigConsts.NAMESPACE_APPLICATION, "miniso.infra-config"})
public class MinisoBootApolloAutoConfiguration implements SmartInitializingSingleton {
    private static final Logger logger = LoggerFactory.getLogger(MinisoBootApolloAutoConfiguration.class);

    private static final Map<String, String> APOLLO_CONFIGURATION_SERVER = new HashMap<>();

    static {
        APOLLO_CONFIGURATION_SERVER.put("dev", String.format("http://%s:30004", DefaultConstants.DEV_HOST));
        APOLLO_CONFIGURATION_SERVER.put("local", "http://127.0.0.1:30004");

        //Test and FAT are the same env.
        APOLLO_CONFIGURATION_SERVER.put("fat", String.format("http://%s:30004", TEST_HOST));
        APOLLO_CONFIGURATION_SERVER.put("test", String.format("http://%s:30004", TEST_HOST));

        APOLLO_CONFIGURATION_SERVER.put("prod", "http://10.0.16.140:30004");

        String configServer = System.getProperty("leqee.apollo.server", "");
        if (StringUtils.isEmpty(configServer)) {
            configServer = APOLLO_CONFIGURATION_SERVER.get(EnvUtil.getEnv());
        }

        System.setProperty("apollo.configService", configServer);
        System.setProperty("apollo.meta", configServer);
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
