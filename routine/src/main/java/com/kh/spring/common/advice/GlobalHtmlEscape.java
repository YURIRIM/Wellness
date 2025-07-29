package com.kh.spring.common.advice;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Field;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.kh.spring.common.annotation.NoHtmlEscape;

@ControllerAdvice
public class GlobalHtmlEscape {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        Object target = binder.getTarget();
        if (target == null) return;

        for (Field field : binder.getTarget().getClass().getDeclaredFields()) {
            if (field.getType() == String.class && !field.isAnnotationPresent(NoHtmlEscape.class)) {
                binder.registerCustomEditor(String.class, field.getName(), new HtmlEscapeEditor());
            }
        }
    }

    public static class HtmlEscapeEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            if (text == null) {
                setValue(null);
            } else {
                setValue(StringEscapeUtils.escapeHtml4(text));
            }
        }
    }
}
