package com.kh.spring.util.common;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.ResizeWebp;

public class BinaryAndBase64 {
	
	
	//base64 safe-url로 인코딩된 문자열을 byte[]로 변경
	public static byte[] base64ToBinary(String base64Str) throws Exception {
	    if (base64Str == null || base64Str.isEmpty()) return null;

	    base64Str = base64Str.replace('-', '+').replace('_', '/');

	    //패딩 없으면 넣기
	    int padding = 4 - (base64Str.length() % 4);
	    if (padding > 0 && padding < 4) {
	        base64Str += "=".repeat(padding);
	    }

	    base64Str = base64Str.replaceAll("\\s+", "");
	    byte[] binary = Base64.getDecoder().decode(base64Str);

	    //바이너리 변경
	    binary = ResizeWebp.resizeWebp(binary);
	    
	    
	    //미리보기
//	    System.out.println("바이너리 길이=" + binary.length);
//	    StringBuilder sb = new StringBuilder();
//	    for (int i = 0; i < Math.min(binary.length, 16); i++) {
//	    	sb.append(String.format("%02X ", binary[i]));
//	    }
//	    System.out.println("바이너리 미리보기=" + sb.toString());
	    
	    
	    if (Exiftool.EXIFTOOL) {
	        binary = Exiftool.sanitizeMetadata(binary);
	    }

	    return binary;
	}
}
