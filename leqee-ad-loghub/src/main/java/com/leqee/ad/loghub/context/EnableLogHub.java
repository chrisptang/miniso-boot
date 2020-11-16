package com.leqee.ad.loghub.context;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(LogHubBeanImportRegistrar.class)
public @interface EnableLogHub {
}
