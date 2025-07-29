package com.kh.spring.util.challenge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

public class ChallengeFix {
	
	//content의 img태그 링크 정상화 및 스크립트 주석처리
	public static String fixScrAndScript(String content, HttpServletRequest request) {
		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();
		String prefix = scheme + "://" + host + ((port == 80 || port == 443) ? "" : ":" + port);

		Pattern pattern = Pattern.compile("(<img[^>]+src=\")(/routine/attachment/select[^\"]*)\"");
		Matcher matcher = pattern.matcher(content);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String beforeSrc = matcher.group(1);
			String srcPath = matcher.group(2);
			matcher.appendReplacement(sb, beforeSrc + prefix + srcPath + "\"");
		}
		matcher.appendTail(sb);
		String fixed = sb.toString();

		// 스크립트 주석처리
		fixed = fixed.replaceAll("(?i)<script", "<!--script");
		fixed = fixed.replaceAll("(?i)</script", "</script-->");

		return fixed;
	}

	// verifyCycleStr만들기
	public static String verifyCycleStr(int verifyCycle) {
		final String[] days = { "월", "화", "수", "목", "금", "토", "일" };
		switch (verifyCycle) {
		case 0:
			return "자유";
		case 201:
			return "매일";
		case 202:
			return "이틀";
		case 203:
			return "사흘";
		case 211:
			return "매주";
		case 212:
			return "2주";
		case 221:
			return "매달";
		default:
			if (verifyCycle > 0 && verifyCycle < 128) {
				StringBuilder sb = new StringBuilder();
				sb.append("매주 ");
				boolean first = true;
				for (int i = 0; i < 7; i++) {
					if ((verifyCycle & (1 << i)) > 0) {
						if (!first)
							sb.append(", ");
						sb.append(days[i]);
						first = false;
					}
				}
				return sb.length() > 0 ? sb.toString() + "요일" : "특정 요일";
			} else {
				return "자유";
			}
		}
	}

	//content에서 태그 삭제
	public static String deleteContentTag(String content) {
		if (content == null || content.equals(""))
			return "";

		String result = content;

		// 1. 주요 멀티미디어 태그 및 하이퍼링크 제거(태그 전체 및 내용 통째로)
		String[] tagsToRemove = { "img", "video", "audio", "iframe", "object", "embed", "svg", "canvas", "source", "a",
				"button", "form", "input", "select", "textarea" };

		for (String tag : tagsToRemove) {
			// 짝태그 (예: <video> ... </video>)
			result = result.replaceAll("(?is)<" + tag + "[^>]*?>.*?</" + tag + "\\s*>", "");
			// 싱글태그 (예: <img />, <input ... />)
			result = result.replaceAll("(?is)<" + tag + "[^>]*?/?>", "");
		}

		// 2. script, style 등 자바스크립트, CSS 태그도 제거
		result = result.replaceAll("(?is)<script[^>]*?>.*?</script\\s*>", "");
		result = result.replaceAll("(?is)<style[^>]*?>.*?</style\\s*>", "");

		// 3. 기타 모든 HTML 태그 제거 (텍스트만 남기기)
		result = result.replaceAll("(?is)<[^>]+>", "");

		// 4. 공백 양쪽 정리
		result = result.trim();

		return result;
	}

}
