package com.leqee.boot.autoconfiguration.cat;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfigProperty;
import com.dianping.cat.log.CatLogger;
import com.dianping.cat.servlet.CatFilter;
import com.leqee.boot.autoconfiguration.common.EnvUtil;
import com.leqee.boot.autoconfiguration.common.LogPathUtil;
import com.wanda.cat.sample.plugins.CatMybatisPlugin;
import net.dubboclub.catmonitor.DubboCat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@ConditionalOnBean({CatBeanImportRegistrar.EnableCatChecker.class})
@PropertySource("classpath:leqee-cat.properties")
@ConfigurationProperties(prefix = "leqee-boot.cat")
public class LeqeeBootCatAutoConfiguration implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(LeqeeBootCatAutoConfiguration.class);

    private static final Map<String, String> CAT_SERVER_LIST = new HashMap<>();

    private static final String DEFAULT_CAT_SERVER = "172.22.15.41";

    static {
        CAT_SERVER_LIST.put("dev", "172.22.15.41");
        CAT_SERVER_LIST.put("fat", "10.0.16.134");
        CAT_SERVER_LIST.put("local", "127.0.0.1");
        CAT_SERVER_LIST.put("prod", "10.0.16.140");
    }

    @Value("${leqee.ad.common.cat.port:2280}")
    private int port;

    @Value("${leqee.ad.common.cat.httpPort:30000}")
    private int httpPort;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Value("${leqee.ad.common.cat.servers}")
    private String[] servers;

    @Bean
    public HealthCheck defaultHealthCheck() {
        return new DefaultHealthCheckImpl();
    }

    @Bean(destroyMethod = "preDestroy")
    @ConditionalOnMissingBean
    public ApplicationHealthCheckBiz applicationHealthCheckBiz() {
        ApplicationHealthCheckBiz healthCheckBiz = new ApplicationHealthCheckBiz();
        return healthCheckBiz;
    }

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

    @Configuration
    @ConditionalOnClass(name = "org.apache.ibatis.plugin.Interceptor")
    @ConditionalOnProperty("spring.datasource.url")
    static class CatMybatisIntegrationAutoConfiguration {
        @Bean(name = "catMybatisPlugin")
        public CatMybatisPlugin catMybatisPlugin() {
            return new CatMybatisPlugin();
        }
    }

    private String[] getCatServers() {
        String serverList = DEFAULT_CAT_SERVER;
        String env = EnvUtil.getEnv();
        if (CAT_SERVER_LIST.containsKey(env)
                && !StringUtils.isEmpty(CAT_SERVER_LIST.get(env))) {
            serverList = CAT_SERVER_LIST.get(env);
        }
        logger.warn("\n\n\n=====\tCAT has been configured as:" + serverList);
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

    private static final AtomicReference<ApplicationContext> APPLICATION_CONTEXT_ATOMIC_REFERENCE =
            new AtomicReference<>();

    @Autowired
    private ApplicationHealthCheckBiz applicationHealthCheckBiz;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT_ATOMIC_REFERENCE.set(applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationHealthCheckBiz.setHealthCheckList(
                APPLICATION_CONTEXT_ATOMIC_REFERENCE.get()
                        .getBeansOfType(HealthCheck.class).values());
        applicationHealthCheckBiz.start();
    }
}
