package com.leqee.boot.dubbo.mockito.beanregistry;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

public class DubboReferenceAnnotationBeanMockitoPostProcessor implements
        ApplicationContextAware, InitializingBean, EnvironmentAware {

    private static final AtomicReference<ApplicationContext> APPLICATION_CONTEXT
            = new AtomicReference<>();

    private static final AtomicReference<Environment> ENVIRONMENT = new AtomicReference<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        injectDubboReferenceBeans();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT.set(applicationContext);
    }

    @Override
    public void setEnvironment(Environment environment) {
        ENVIRONMENT.set(environment);
    }

    private void injectDubboReferenceBeans() {
        String[] beanNames = APPLICATION_CONTEXT.get().getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = APPLICATION_CONTEXT.get().getBean(beanName);
            Class<?> beanType = bean.getClass();
            Field[] fields = beanType.getDeclaredFields();
            for (Field field : fields) {
                DubboReference[] annotations = field.getAnnotationsByType(DubboReference.class);
                if (annotations != null && annotations.length == 1) {
                    //Inject DubboReference field with beans defined in current spring ApplicationContext;
                    injectBeanField(bean, field);
                }
            }
        }
    }

    private void injectBeanField(Object beanInstance, Field fieldToInject) {
        Class<?> fieldType = fieldToInject.getType();
        Object beanToUse = APPLICATION_CONTEXT.get().getBean(fieldType);
        fieldToInject.setAccessible(true);
        try {
            fieldToInject.set(beanInstance, beanToUse);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    String.format("Unable to inject Mockito bean:%s, annotated field:%s"
                            , beanInstance.getClass().getName(), fieldToInject.getName()));
        }
    }
}
