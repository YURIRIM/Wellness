package com.kh.spring.common.advice;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Field;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.kh.spring.common.annotation.NoXssSanitizer;

@ControllerAdvice
public class GlobalXssSanitizer {

    private static final PolicyFactory POLICY =
            Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);

    private static class XssSanitizerEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            setValue(text == null ? null : POLICY.sanitize(text));
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        Object target = binder.getTarget();   // 현재 바인딩 중인 객체(예: Board, Member 등)

        if (target == null) {
            return;
        }

        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() != String.class) {
                continue;
            }

            if (field.isAnnotationPresent(NoXssSanitizer.class)) {
                continue;
            }

            binder.registerCustomEditor(
                    String.class,               // 적용 타입
                    field.getName(),            // property path(필드명)
                    new XssSanitizerEditor()    // 수행 로직
            );
        }
    }
}
