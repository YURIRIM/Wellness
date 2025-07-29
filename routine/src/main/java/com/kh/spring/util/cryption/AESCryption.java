package com.kh.spring.util.cryption;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCryption {
	
    /**
     * 암호화 파일을 AES-128 CBC로 복호화
     * 
     * @param filePath 복호화할 파일 경로 (Base64 인코딩된 파일)
     * @param keyStr 복호화 키 문자열 (SHA-256 해시 후 앞 16바이트 사용)
     * @return 복호화된 평문 텍스트
     */
    public static String decryptionFile(String filePath, String keyStr) throws Exception {
        //복호화 키 생성: SHA-256 해시 후 앞 16바이트
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(keyStr.getBytes(StandardCharsets.UTF_8));
        byte[] key = Arrays.copyOf(hash, 16);

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        //파일에서 Base64 인코딩 데이터 읽기 및 디코딩
        Path path = Path.of(filePath);
        byte[] base64Data = Files.readAllBytes(path);
        byte[] ivAndEncrypted = Base64.getDecoder().decode(base64Data);

        //IV(앞 16바이트)와 암호문 분리
        byte[] iv = Arrays.copyOfRange(ivAndEncrypted, 0, 16);
        byte[] encrypted = Arrays.copyOfRange(ivAndEncrypted, 16, ivAndEncrypted.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //Cipher 객체 생성 및 초기화 (복호화 모드)
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        //복호화
        byte[] decryptedBytes = cipher.doFinal(encrypted);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
