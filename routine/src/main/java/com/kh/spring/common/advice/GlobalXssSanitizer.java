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
	private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// String 타입 전체에 커스텀 에디터 등록
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				Object target = binder.getTarget();
				if (target != null) {
					// 모든 declared fields 순회
					for (Field field : target.getClass().getDeclaredFields()) {
						// 필드가 String 타입 + setAccessible! (private 변수도 검사)
						if (field.getType() == String.class) {
							field.setAccessible(true);
							try {
								Object value = field.get(target);
								if (value == text) {
									// 해당 필드에 NoXssSanitizer가 붙어 있으면 원본 값 반환(소독 생략)
									if (field.isAnnotationPresent(NoXssSanitizer.class)) {
										setValue(text);
										return;
									}
								}
							} catch (Exception e) {
								// 무시
							}
						}
					}
				}
				// 기본(어노테이션 없으면 무조건 소독)
				setValue(text == null ? null : POLICY.sanitize(text));
			}
		});
	}
}
