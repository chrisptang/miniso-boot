package com.leqee.boot.autoconfiguration.annotation;

import com.leqee.boot.autoconfiguration.dubbo.DubboBeanImportRegistrar;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@DubboComponentScan
@Import(DubboBeanImportRegistrar.class)
public @interface EnableLeqeeDubbo {

    /**
     * Base packages to scan for annotated @Service classes.
     * <p>
     * package names.
     *
     * @return the base packages to scan
     * @see DubboComponentScan#basePackages()
     */
    @AliasFor(annotation = DubboComponentScan.class, attribute = "basePackages")
    String[] basePackages() default {};
}
