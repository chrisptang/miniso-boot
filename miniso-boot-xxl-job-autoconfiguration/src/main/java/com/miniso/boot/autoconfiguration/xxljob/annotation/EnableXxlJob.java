package com.miniso.boot.autoconfiguration.xxljob.annotation;

import com.miniso.boot.autoconfiguration.xxljob.beanregistry.XxlJobBeanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(XxlJobBeanRegistrar.class)
public @interface EnableXxlJob {
}
