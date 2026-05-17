package com.bist.mini.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JWT 토큰에서 회원 정보를 추출하여 주입하는 어노테이션
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginMember {
    /**
     * 필수 여부 (true인 경우 토큰이 없으면 예외 발생)
     */
    boolean required() default true;
}
