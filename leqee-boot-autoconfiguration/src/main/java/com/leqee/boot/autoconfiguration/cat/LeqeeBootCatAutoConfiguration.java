package com.leqee.boot.autoconfiguration.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfigProperty;
import com.dianping.cat.log.CatLogger;
import com.dianping.cat.servlet.CatFilter;
import com.leqee.boot.autoconfiguration.common.LogPathUtil;
import net.dubboclub.catmonitor.DubboCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnClass(name = {"com.leqee.boot.autoconfiguration.annotation.EnableCat"})
@PropertySource("classpath:leqee-cat.properties")
@ConfigurationProperties(prefix = "leqee-boot.cat")
public class LeqeeBootCatAutoConfiguration {

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
        property.setServers(servers);

        Cat.initialize(property);

        DubboCat.enable();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        CatFilter filter = new CatFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("cat-filter");
        registration.setOrder(1);

        return registration;
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
