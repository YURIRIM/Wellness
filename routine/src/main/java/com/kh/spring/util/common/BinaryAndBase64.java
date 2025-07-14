package com.kh.spring.util.common;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.ResizeWebp;

public class BinaryAndBase64 {
	
	
	//base64로 인코딩된 문자열을 byte[]로 변경
	public static byte[] Base64ToBinary(String base64Str) throws Exception{
		if (base64Str == null || base64Str.equals("")) return null;
		
	    //브라우저가 지멋대로 base64주물럭댔으면 수정
		base64Str = URLDecoder.decode(base64Str, StandardCharsets.UTF_8);
		base64Str = base64Str.replace("&#43;", "+");
	    base64Str = base64Str.replaceAll("\\s+", "");
	    base64Str = base64Str.replaceAll("[^A-Za-z0-9+/=]", "");

	    byte[] binary = Base64.getDecoder().decode(base64Str);
	    
	    binary = ResizeWebp.resizeWebp(binary);
	    
	    if (Exiftool.EXIFTOOL) {
	        binary = Exiftool.sanitizeMetadata(binary);
	    }

	    return binary;
	}
}
