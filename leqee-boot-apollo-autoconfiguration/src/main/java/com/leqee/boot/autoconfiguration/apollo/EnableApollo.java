package com.leqee.boot.autoconfiguration.apollo;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ApolloBeanImportRegistrar.class)
public @interface EnableApollo {
}
