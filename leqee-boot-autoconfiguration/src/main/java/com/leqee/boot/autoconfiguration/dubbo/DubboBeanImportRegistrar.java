package com.leqee.boot.autoconfiguration.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class DubboBeanImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("enableLeqeeDubboChecker", new RootBeanDefinition(EnableLeqeeDubboChecker.class));
    }

    public static class EnableLeqeeDubboChecker {
        public EnableLeqeeDubboChecker() {
            log.info("Dubbo is enabled..");
        }
    }
}
