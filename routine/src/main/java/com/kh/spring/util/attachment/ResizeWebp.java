package com.kh.spring.util.attachment;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.kh.spring.util.common.Regexp;
import com.luciad.imageio.webp.WebPWriteParam;

public class ResizeWebp {
	
	//바이너리 사진 webp로 변경 및 리사이즈
	public static byte[] resizeWebp(byte[] imageBytes) throws Exception {
		//바이너리 데이터 옴뇸뇸해서 읽어오기
		BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
		if (inputImage == null) {
			throw new IllegalArgumentException("이미지를 읽을 수 없습니다. (지원 포맷: JPG, PNG, WebP 등)");
		}

		int width = inputImage.getWidth();
		int height = inputImage.getHeight();

		//리사이즈
		final long MAX_SIZE = Regexp.MAX_ATTACHMENT_FILE_SIZE;
		final int MAX_WIDTH = Regexp.ATTACHMENT_FILE_WIDTH;
		final int MAX_HEIGHT = Regexp.ATTACHMENT_FILE_HEIGHT;

		boolean needResize = width > MAX_WIDTH || height > MAX_HEIGHT;
		int targetWidth = width, targetHeight = height;

		if (needResize) {
			double widthRatio = (double) MAX_WIDTH / width;
			double heightRatio = (double) MAX_HEIGHT / height;
			double scale = Math.min(widthRatio, heightRatio);
			targetWidth = (int) Math.round(width * scale);
			targetHeight = (int) Math.round(height * scale);
		}

		BufferedImage outputImage;
		if (needResize) {
			outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = outputImage.createGraphics();
			try {
				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2d.drawImage(inputImage, 0, 0, targetWidth, targetHeight, null);
			} finally {
				g2d.dispose();
			}
		} else {
			outputImage = inputImage;
		}

		//포맷 변경
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

		try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
			writer.setOutput(ios);

			WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
			writeParam.setCompressionMode(WebPWriteParam.MODE_EXPLICIT);
			writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
			writeParam.setCompressionQuality(1.0f);

			writer.write(null, new IIOImage(outputImage, null, null), writeParam);
		} finally {
			if (writer != null) {
				writer.dispose();
			}
		}
		byte[] resultBytes = baos.toByteArray();
		if (resultBytes.length > MAX_SIZE) {
			throw new IllegalArgumentException("리사이즈 후에도 파일 크기가 허용 범위를 초과합니다.");
		}

		return resultBytes;
	}

}