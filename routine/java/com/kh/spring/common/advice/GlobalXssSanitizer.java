package com.kh.spring.common.advice;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import java.beans.PropertyEditorSupport;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

//모든 입력받는 문자열 자료형에 대해 XSS소독
@ControllerAdvice
public class GlobalXssSanitizer {
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null) {
                    setValue(null);
                } else {
                    setValue(POLICY.sanitize(text));
                }
            }
        });
    }
}
