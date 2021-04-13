package com.miniso.boot.autoconfiguration.apollo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class ApolloBeanImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition(EnableApolloChecker.class.getName(), new RootBeanDefinition(EnableApolloChecker.class));
    }

    public static class EnableApolloChecker {
        public EnableApolloChecker() {
            log.info("Apollo is enabled...");
        }
    }
}
