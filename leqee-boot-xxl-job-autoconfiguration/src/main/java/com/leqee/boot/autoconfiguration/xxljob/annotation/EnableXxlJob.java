package com.leqee.boot.autoconfiguration.xxljob.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableXxlJob {
}
