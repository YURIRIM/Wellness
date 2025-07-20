package com.kh.spring.util.attachment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class Watermark {

	// 낙인 - 기본형
	public static byte[] defaultStigma(byte[] fileContent, String nick) throws Exception {
		System.out.println("기본형 낙인 찍는중... 가슴을 데인 것 처럼 눈물에 베인 것 처럼");
		
		// 1. webp 바이트 -> BufferedImage
		ByteArrayInputStream in = new ByteArrayInputStream(fileContent);
		BufferedImage image = ImageIO.read(in);

		// 2. 그래픽스 준비 (투명 배경)
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 폰트 및 색상(반투명 흰색)
		InputStream fontStream = Watermark.class.getResourceAsStream("/config/maple.ttf");
		Font mapleFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
		Font font = mapleFont.deriveFont(Font.BOLD, image.getWidth() / 15);
		g2d.setFont(font);
		g2d.setColor(new Color(255, 255, 255, 180));

		// 워터마크 문구 생성
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
		String mark = "ROUTINE - " + nick + ", " + time;

		// 텍스트 위치 중앙, 비스듬히 (45도)
		FontRenderContext frc = g2d.getFontRenderContext();
		int textWidth = (int) font.getStringBounds(mark, frc).getWidth();
		int textHeight = (int) font.getStringBounds(mark, frc).getHeight();

		int cx = image.getWidth() / 2;
		int cy = image.getHeight() / 2;

		AffineTransform orig = g2d.getTransform();
		g2d.rotate(Math.toRadians(-23), cx, cy); // 약간 비스듬히

		g2d.drawString(mark, cx - textWidth / 2, cy + textHeight / 2);
		g2d.setTransform(orig);
		g2d.dispose();

		// 3. 다시 webp로 인코딩
		return writeWebpToBytes(image);
	}

	// 낙인 - 개인형
	public static byte[] customStigma(byte[] fileContent, byte[] watermark) throws Exception {
		System.out.println("개인형 낙인 찍는중... 지워지지 않는 상처들이 괴롭다");

		// 1. 원본, 워터마크 이미지 읽기
		BufferedImage base = ImageIO.read(new ByteArrayInputStream(fileContent));
		BufferedImage wm = ImageIO.read(new ByteArrayInputStream(watermark));

		// 워터마크 크기 조절(필요시)
		int targetW = base.getWidth() / 5;
		int targetH = wm.getHeight() * targetW / wm.getWidth();
		Image wmResized = wm.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);

		// 워터마크 그리기 (우하단, 살짝 투명)
		Graphics2D g = base.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 투명도 50%
		int x = base.getWidth() - targetW - 20;
		int y = base.getHeight() - targetH - 20;
		g.drawImage(wmResized, x, y, null);
		g.dispose();

		// 2. 다시 webp로 인코딩
		return writeWebpToBytes(base);
	}

	// BufferedImage를 webp 형식 byte[]로 변환
	private static byte[] writeWebpToBytes(BufferedImage image) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
			writer.setOutput(ios);
			writer.write(image);
		}
		writer.dispose();
		return out.toByteArray();
	}

}
