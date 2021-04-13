package com.miniso.boot.dubbo.mockito.annotation;

import com.miniso.boot.dubbo.mockito.beanregistry.DubboReferenceMockitoRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 用来Mock Dubbo远程服务，仅用于测试用例；
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(DubboReferenceMockitoRegistrar.class)
public @interface MockDubboReference {
}
