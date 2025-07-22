package com.kh.spring.util.attachment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.kh.spring.challenge.model.vo.Attachment;

/*
 * exiftool 외부 툴
 * https://exiftool.org/
 * exiftool.exe (-k어쩌구로 되어 있으면 이름 바꾸기)
 * 해당 파일 위치를 환경 변수 경로로 잡기 
 */
public class Exiftool {
	
	//exiftool 작동 하니?
	public static final boolean EXIFTOOL = checkExiftool();
	
	public static boolean checkExiftool(){
		try {
			ProcessBuilder pbCheck = new ProcessBuilder("exiftool", "-ver");
			Process procCheck = pbCheck.start();
			int checkResult = procCheck.waitFor();
			
			//exiftool 안 열리는데요??
			if (checkResult != 0) return false;
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/*
	 * 메타데이터 열어보자
	 * 반환값 - 1: 정상, 2: 직접 촬영 아님, 0: 기타 오류
	 */
	public static int inspectAttachment(Attachment at) throws Exception {
		//임시 파일 생성
		File tempFile = File.createTempFile("upload_", ".webp");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			fos.write(at.getFileContent());
		}

		//메타데이터 자 자 이리로 왓
		ProcessBuilder pb = new ProcessBuilder("exiftool", tempFile.getAbsolutePath());
		pb.redirectErrorStream(true);
		Process proc = pb.start();

		StringBuilder output = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line.toLowerCase()).append("\n");
			}
		}
		int exitCode = proc.waitFor();
		tempFile.delete();

		if (exitCode != 0) return 0; //뭔가 이상해요

		String meta = output.toString();

		//메타데이터 판별

		//메타데이터가 비었거나
		if (meta.trim().isEmpty() || meta.contains("no exif data found")) {
			return 2;
		}
		//스크린샷으로 추정되는 키워드가 있거나
		if (meta.contains("screenshot") || meta.contains("capture") || meta.contains("snipping")) {
			return 2;
		}
		//카메라 제조사/모델 등 주요 촬영 정보가 없으면 가세요라
		if (!meta.contains("make") && !meta.contains("model") && !meta.contains("software")) {
			return 2;
		}

		return 1;
	}

	//메타데이터 소각 
	public static byte[] sanitizeMetadata(byte[] picture) throws Exception {
		System.out.println("exiftool로 메타데이터 때치때치 중... 사진 길이 = "+picture.length);
		
		//임시 파일 생성 및 byte[] 저장
		File tempInput = File.createTempFile("input_", ".webp");
		try (FileOutputStream fos = new FileOutputStream(tempInput)) {
			fos.write(picture);
		}

		//메타데이터 소각
		// exiftool -all= -overwrite_original input.webp
		ProcessBuilder pb = new ProcessBuilder("exiftool", "-all=", "-overwrite_original", tempInput.getAbsolutePath());
		pb.redirectErrorStream(true);
		Process process = pb.start();
		int result = process.waitFor();
		if (result != 0) {
			tempInput.delete();
			InputStream is = process.getInputStream();
			String output = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			throw new Exception("exiftool 실행 오류, "+output);
		}

		//소각된 파일을 다시 byte[]로 읽어 Attachment.file에 슛
		byte[] sanitizedData = Files.readAllBytes(tempInput.toPath());

		//이제 볼일 없는 임시파일 가세요라
		tempInput.delete();
		
		return sanitizedData;
	}
}
