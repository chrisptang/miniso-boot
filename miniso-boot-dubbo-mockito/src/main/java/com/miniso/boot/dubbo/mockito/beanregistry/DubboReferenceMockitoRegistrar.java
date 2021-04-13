package com.miniso.boot.dubbo.mockito.beanregistry;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class DubboReferenceMockitoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registry.registerBeanDefinition(DubboReferenceAnnotationBeanMockitoPostProcessor.class.getName()
                , new RootBeanDefinition(DubboReferenceAnnotationBeanMockitoPostProcessor.class));
    }
}
