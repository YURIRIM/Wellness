package com.kh.spring.util.challenge;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

public class ChallengeFix {
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

	    //스크립트 주석처리
	    fixed = fixed.replaceAll("(?i)<script", "<!--script");
	    fixed = fixed.replaceAll("(?i)</script", "</script-->");

	    return fixed;
	}
	
	//verifyCycleStr만들기
	public static String verifyCycleStr(int verifyCycle) {
	    final String[] days = {"월", "화", "수", "목", "금", "토", "일"};
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
	            if(verifyCycle > 0 && verifyCycle < 128) {
	                StringBuilder sb = new StringBuilder();
	                sb.append("매주 ");
	                boolean first = true;
	                for (int i = 0; i < 7; i++) {
	                    if ((verifyCycle & (1 << i)) > 0) {
	                        if (!first) sb.append(", ");
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



}
