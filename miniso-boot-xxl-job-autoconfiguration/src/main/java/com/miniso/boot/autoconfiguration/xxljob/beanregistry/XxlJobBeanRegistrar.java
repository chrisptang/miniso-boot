package com.miniso.boot.autoconfiguration.xxljob.beanregistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class XxlJobBeanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        register(registry);
    }

    private void register(BeanDefinitionRegistry registry) {
        BeanDefinition bd = new RootBeanDefinition(XxlJobBeanRegistrar.EnableXxlJobChecker.class);
        registry.registerBeanDefinition("enableXxlJobChecker", bd);
    }

    public static class EnableXxlJobChecker {
        public EnableXxlJobChecker() {
            log.info("XXL-JOB is enabled..");
        }
    }
}
