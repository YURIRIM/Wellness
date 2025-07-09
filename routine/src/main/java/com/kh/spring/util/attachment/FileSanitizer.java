package com.kh.spring.util.attachment;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.util.common.Regexp;

//바이너리 webp 파일 이름/확장자 소독하기
public class FileSanitizer {

	public static boolean attachmentSanitizer(MultipartFile file, Attachment at) throws Exception {
		// 파일이 비어있는지 체크
		if (file.isEmpty()) {
			return false;
		}

		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.trim().isEmpty()) {
			return false;
		}

		// 파일명 보안 처리
		String sanitizedFilename = sanitizeFileName(originalFilename);

		// 확장자 검증
		String extension = FilenameUtils.getExtension(sanitizedFilename).toLowerCase();
		List<String> allowedExtensions = Arrays.asList("webp");

		if (!allowedExtensions.contains(extension)) {
			return false;
		}

		// MIME 타입 검증
		String contentType = file.getContentType();
		if (contentType == null || !isValidMimeType(contentType, extension)) {
			return false;
		}

		// Magic Bytes 검증 (실제 파일 내용 확인)
		if (!validateFileContent(file.getBytes(), extension)) {
			return false;
		}

		// 저장!
		at = Attachment.builder().file(file.getBytes()).fileName(sanitizedFilename).fileSize((int) file.getSize())
				.build();
		return true;
	}

	// 파일 이름 소독
	public static String sanitizeFileName(String filename) throws Exception {
		if (filename == null || filename.trim().isEmpty()) {
			throw new IllegalArgumentException("파일명이 비어있습니다.");
		}

		// 1. 파일 이름 유니코드 정규화 (NFC)
		filename = Normalizer.normalize(filename, Normalizer.Form.NFC);

		// 2. 경로 제거 (path traversal 방지)
		filename = FilenameUtils.getName(filename);

		// 3. NULL 바이트 제거 (null byte injection 방지)
		filename = filename.replaceAll("\u0000", "");
		filename = filename.replaceAll("\\x00", "");

		// 4. 제어 문자 제거 (0x00-0x1F, 0x7F-0x9F)
		filename = filename.replaceAll("[\\p{Cntrl}]", "");

		// 5. 특수문자 가세요라
		// \ / : * ? " < > |
		filename = filename.replaceAll("[\\\\/:*?\"<>|]", "_");

		// 6. 앞뒤 공백, 마침표 제거
		filename = filename.trim().replaceAll("^\\.+", "").replaceAll("\\s+$", "");

		// 7. Windows 예약어 체크 및 처리
		String nameWithoutExt = FilenameUtils.removeExtension(filename);
		List<String> reservedNames = Arrays.asList("CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5",
				"COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");

		if (reservedNames.contains(nameWithoutExt.toUpperCase())) {
			nameWithoutExt = nameWithoutExt + "_safe";
			String extension = FilenameUtils.getExtension(filename);
			filename = nameWithoutExt + (extension.isEmpty() ? "" : "." + extension);
		}

		// 8. 연속된 공백이나 특수문자를 하나로 축소
		filename = filename.replaceAll("\\s+", "_");
		filename = filename.replaceAll("_{2,}", "_");

		// 9. 최종 유효성 검사: 영문자, 숫자, 언더스코어, 마침표, 하이픈만 허용
		if (!filename.matches(Regexp.ATTACHMENT_FILE_NAME)) {
			// 허용되지 않는 문자를 언더스코어로 치환
			filename = filename.replaceAll(Regexp.ATTACHMENT_FILE_NAME, "_");
		}

		// 10. 파일명이 완전히 비어있거나 마침표만 남은 경우 처리
		if (filename.isEmpty() || filename.matches("^\\.+$")) {
			throw new IllegalArgumentException("유효하지 않은 파일명입니다.");
		}

		return filename;
	}

	// MIME 타입 검증
	private static boolean isValidMimeType(String contentType, String extension) {
		return "webp".equalsIgnoreCase(extension) && "image/webp".equalsIgnoreCase(contentType);
	}

	// Magic Bytes 검증
	private static boolean validateFileContent(byte[] fileBytes, String extension) {
		if (!"webp".equalsIgnoreCase(extension))
			return false;
		if (fileBytes.length < 12)
			return false;

		// 'RIFF'
		if (fileBytes[0] != 0x52 || fileBytes[1] != 0x49 || fileBytes[2] != 0x46 || fileBytes[3] != 0x46)
			return false;
		// 'WEBP'
		if (fileBytes[8] != 0x57 || fileBytes[9] != 0x45 || fileBytes[10] != 0x42 || fileBytes[11] != 0x50)
			return false;

		return true;
	}
}
