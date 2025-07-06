package com.kh.spring.util.challenge;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.kh.spring.util.common.Regexp;
import com.luciad.imageio.webp.WebPWriteParam;

//webp 리사이즈
public class ResizeWebp {

	public static byte[] resizeWebp(byte[] webpBytes) throws Exception {
		// 1. WebP 이미지를 BufferedImage로 읽기
		BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(webpBytes));
		if (inputImage == null) {
			throw new IllegalArgumentException("WebP 이미지를 읽을 수 없습니다.");
		}

		int width = inputImage.getWidth();
		int height = inputImage.getHeight();

		// 2. 정규식 이리와
		final long MAX_SIZE = Regexp.MAX_ATTACHMENT_FILE_SIZE;
		final int MAX_WIDTH = Regexp.ATTACHMENT_FILE_WIDTH;
		final int MAX_HEIGHT = Regexp.ATTACHMENT_FILE_HEIGHT;

		// 3. 리사이즈가 필요한가요??
		boolean needResize = false;
		int targetWidth = width;
		int targetHeight = height;

		if (width > MAX_WIDTH || height > MAX_HEIGHT) {
			needResize = true;
			// 비율 유지하면서 제한값에 맞게 축소
			double widthRatio = (double) MAX_WIDTH / width;
			double heightRatio = (double) MAX_HEIGHT / height;
			double scale = Math.min(widthRatio, heightRatio);
			targetWidth = (int) Math.round(width * scale);
			targetHeight = (int) Math.round(height * scale);
		}

		// 4. 리사이즈 또는 동일 화질로 재인코딩
		BufferedImage outputImage;
		if (needResize) {
			outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = outputImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.drawImage(inputImage, 0, 0, targetWidth, targetHeight, null);
			g2d.dispose();
		} else {
			// 리사이즈 필요 없으면 원본 이미지 그대로 사용 (재인코딩)
			outputImage = inputImage;
		}

		// 5. WebP로 저장 (메모리 스트림 사용, 화질 최대한 보존)
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
		WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
		writeParam.setCompressionMode(WebPWriteParam.MODE_EXPLICIT);
		// 손실 압축(기본), 화질 최대(1.0f)로 설정
		writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
		writeParam.setCompressionQuality(1.0f);

		writer.setOutput(new MemoryCacheImageOutputStream(baos));
		writer.write(null, new IIOImage(outputImage, null, null), writeParam);
		writer.dispose();

		byte[] resultBytes = baos.toByteArray();

		// 6. 파일 크기 제한 검증
		if (resultBytes.length > MAX_SIZE) {
			throw new IllegalArgumentException("리사이즈 후에도 파일 크기가 허용 범위를 초과합니다.");
		}

		return resultBytes;
	}
}
