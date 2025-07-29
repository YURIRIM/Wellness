package com.kh.spring.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)//적용 범위 : 필드
@Retention(RetentionPolicy.RUNTIME)//유지 기간 : 런타임
public @interface NoHtmlEscape {//어노테이션 이름
}
