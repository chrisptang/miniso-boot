package com.leqee.ad.loghub.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class LogHubBeanImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("LogHubChecker", new RootBeanDefinition(LogHubChecker.class));
    }

    public static class LogHubChecker {
        public LogHubChecker() {
            log.info("LogHub is enabled...");
        }
    }
}
