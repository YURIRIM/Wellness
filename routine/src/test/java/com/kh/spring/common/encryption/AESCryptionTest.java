package com.kh.spring.common.encryption;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AESCryptionTest {

	/**
	 * 파일을 AES-128 CBC로 인코딩
	 * resouces/config 경로의 파일 이름과 key 입력
	 * 열쇠는 SHA-256 후 앞 16바이트만 사용됨
	 * 열쇠로 인코딩 후 vi벡터를 암호문 앞에 둠
	 */
	@Test
	public void encryptionFile() {
		try (Scanner sc = new Scanner(System.in)){
			String folderPath = "src/main/resources/config";
			System.out.println("*****초간단 AES-128 CBC 암호화하기*****");
			System.out.print("파일 이름을 입력해! : ");
			String fileName = sc.nextLine();
			System.out.print("열쇠를 입력해! : ");
			String keyStr = sc.nextLine();
			
			//IV 준비
	        SecureRandom random = new SecureRandom();
	        byte[] iv = new byte[16];
	        random.nextBytes(iv);
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
			
			//SHA-256 해시 후 앞 16바이트만 남기기
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(keyStr.getBytes(StandardCharsets.UTF_8));
			byte[] key = Arrays.copyOf(hash, 16);

			//파일 읽어오기
			Path filePath = Paths.get(folderPath, fileName);
			byte[] fileBytes;
			fileBytes = Files.readAllBytes(filePath);
			
			//key와 IV로 AES 암호화
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
	        byte[] encrypted = cipher.doFinal(fileBytes);

	        //[IV + 암호문] 저장 → 복호화 시 IV 분리
	        byte[] ivAndEncrypted = new byte[iv.length + encrypted.length];
	        System.arraycopy(iv, 0, ivAndEncrypted, 0, iv.length);
	        System.arraycopy(encrypted, 0, ivAndEncrypted, iv.length, encrypted.length);

	        //Base64 인코딩 후 파일로 저장
	        byte[] base64 = Base64.getEncoder().encode(ivAndEncrypted);
	        String encryptedFileName = fileName + ".enc";
	        Path encryptedPath = Paths.get(folderPath, encryptedFileName);
	        Files.write(encryptedPath, base64);
			
			System.out.println("암호화 완료! 네 비밀번호 '"+keyStr+"'를 잊지 마!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
