package com.leqee.boot.autoconfiguration.cat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class CatBeanImportRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition("enableCatChecker", new RootBeanDefinition(EnableCatChecker.class));
    }

    public static class EnableCatChecker {
        public EnableCatChecker() {
            log.info("CAT is enabled..");
        }
    }
}
