package com.miniso.boot.autoconfiguration.annotation;

import com.miniso.boot.autoconfiguration.cat.CatBeanImportRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(CatBeanImportRegistrar.class)
public @interface EnableCat {
}
