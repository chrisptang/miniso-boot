package com.leqee.boot.autoconfiguration.xxljob.annotation;

import com.leqee.boot.autoconfiguration.xxljob.beanregistry.XxlJobBeanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(XxlJobBeanRegistrar.class)
public @interface EnableXxlJob {
}
