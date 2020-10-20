package com.leqee.boot.autoconfiguration.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfigProperty;
import com.dianping.cat.log.CatLogger;
import com.dianping.cat.servlet.CatFilter;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import com.leqee.boot.autoconfiguration.common.LogPathUtil;
import net.dubboclub.catmonitor.DubboCat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnClass(name = {"com.leqee.boot.autoconfiguration.annotation.EnableCat"})
@PropertySource("classpath:leqee-cat.properties")
@ConfigurationProperties(prefix = "leqee-boot.cat")
public class LeqeeBootCatAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(LeqeeBootCatAutoConfiguration.class);

    private static final Map<String, String> CAT_SERVER_LIST = new HashMap<>();

    private static final String DEFAULT_CAT_SERVER = "172.22.15.41";

    static {
        CAT_SERVER_LIST.put("dev", "172.22.15.41");
        CAT_SERVER_LIST.put("fat", "10.0.16.134");
        CAT_SERVER_LIST.put("local", "127.0.0.1");
        CAT_SERVER_LIST.put("prod", "10.0.16.134");
    }

    private int port;

    private int httpPort;

    private String[] servers;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Autowired
    private LeqeeBootCatAutoConfiguration catAutoConfiguration;

    @Bean
    public FilterRegistrationBean catFilter() {
        initCatLogger();

        ClientConfigProperty property = new ClientConfigProperty();
        property.setAppId(applicationName);
        property.setPort(port);
        property.setHttpPort(httpPort);
        if (null == servers || servers.length <= 0) {
            property.setServers(getCatServers());
        } else {
            property.setServers(servers);
        }

        Cat.initialize(property);

        if (logger.isInfoEnabled()) {
            logger.info("Is CAT initialized:" + Cat.isInitialized());
        }

        DubboCat.enable();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatFilter filter = new CatFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);


        return registration;
    }

    private String[] getCatServers() {
        String serverList = DEFAULT_CAT_SERVER;
        String env = EnvUtil.getEnv();
        if (CAT_SERVER_LIST.containsKey(env)
                && !StringUtils.isEmpty(CAT_SERVER_LIST.get(env))) {
            serverList = CAT_SERVER_LIST.get(env);
        }
        return serverList.split(",");
    }

    private void initCatLogger() {
        //初始化CAT本地日志；
        CatLogger.updateLogHome(LogPathUtil.ensureLogPath(applicationName, "cat"));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public String[] getServers() {
        return servers;
    }

    public void setServers(String[] servers) {
        this.servers = servers;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
