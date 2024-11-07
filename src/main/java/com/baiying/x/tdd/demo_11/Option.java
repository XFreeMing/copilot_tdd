package com.baiying.x.tdd.demo_11;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER }) // 因为是用在 recordconstructor的 parameter上
public @interface Option {
    String value();
}