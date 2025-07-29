package com.kh.spring.util.attachment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.kh.spring.util.common.Regexp;
import com.luciad.imageio.webp.WebPWriteParam;

public class ResizeWebp {
	public static byte[] resizeWebp(byte[] imageBytes) throws Exception {
		BufferedImage currentImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
		if (currentImage == null) {
			throw new IllegalArgumentException("지원 포맷이 아닙니다. JPG/PNG 등만 지원합니다.");
		}

		final long MAX_SIZE = Regexp.MAX_ATTACHMENT_FILE_SIZE;
		final int MAX_W = Regexp.ATTACHMENT_FILE_WIDTH;
		final int MAX_H = Regexp.ATTACHMENT_FILE_HEIGHT;
		final int MAX_LOOP = Regexp.AT_RESIZE_MAX_LOOP;
		float quality = 1.0f;
		double shrinkRat = 0.9;

		for (int loop = 0; loop < MAX_LOOP; loop++) {
			// 항상 JPEG 호환 RGB 3채널로 변환
			BufferedImage jpegCompatible = toJpegCompatible(currentImage);
			int w = jpegCompatible.getWidth(), h = jpegCompatible.getHeight();

			ByteArrayOutputStream tmpBaos = new ByteArrayOutputStream();
			ImageWriter jpegW = ImageIO.getImageWritersByFormatName("jpeg").next();
			try (ImageOutputStream ios = ImageIO.createImageOutputStream(tmpBaos)) {
				jpegW.setOutput(ios);
				ImageWriteParam p = jpegW.getDefaultWriteParam();
				p.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				p.setCompressionQuality(quality);
				jpegW.write(null, new IIOImage(jpegCompatible, null, null), p);
			} finally {
				jpegW.dispose();
			}
			byte[] jpegBytes = tmpBaos.toByteArray();

			// 용량·치수 검사
			boolean overSize = jpegBytes.length > MAX_SIZE;
			boolean overDim = w > MAX_W || h > MAX_H;
			if (!(overSize || overDim)) {
				break;
			}

			double wR = (double) MAX_W / w, hR = (double) MAX_H / h;
			double scale = Math.min(Math.min(wR, hR), shrinkRat);
			int tw = (int) Math.round(w * scale);
			int th = (int) Math.round(h * scale);

			// 다음 currentImage 준비 (항상 TYPE_INT_RGB)
			BufferedImage resized = new BufferedImage(tw, th, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = resized.createGraphics();
			try {
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.drawImage(jpegCompatible, 0, 0, tw, th, Color.WHITE, null);
			} finally {
				g2.dispose();
			}
			currentImage = resized;
			quality = Math.max(0.4f, quality - 0.15f);
		}

		// 마지막 단계에서도 RGB 3채널 변환 (webp도 마찬가지)
		BufferedImage webpCompatible = toJpegCompatible(currentImage);

		ByteArrayOutputStream finalBaos = new ByteArrayOutputStream();
		ImageWriter webpW = ImageIO.getImageWritersByMIMEType("image/webp").next();
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(finalBaos)) {
			webpW.setOutput(ios);
			WebPWriteParam wp = new WebPWriteParam(webpW.getLocale());
			wp.setCompressionMode(WebPWriteParam.MODE_EXPLICIT);
			wp.setCompressionType("Lossy");
			wp.setCompressionQuality(quality);
			webpW.write(null, new IIOImage(webpCompatible, null, null), wp);
		} finally {
			webpW.dispose();
		}

		return finalBaos.toByteArray();
	}

	private static BufferedImage toJpegCompatible(BufferedImage src) {
		if (src.getType() == BufferedImage.TYPE_INT_RGB)
			return src;
		BufferedImage rgbImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = rgbImage.createGraphics();
		try {
			g.drawImage(src, 0, 0, Color.WHITE, null);
		} finally {
			g.dispose();
		}
		return rgbImage;
	}
}