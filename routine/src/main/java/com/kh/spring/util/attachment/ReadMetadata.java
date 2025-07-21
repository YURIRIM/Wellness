package com.kh.spring.util.attachment;

import java.io.File;
import java.io.FileOutputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.kh.spring.challenge.model.vo.Attachment;

public class ReadMetadata {
	public static int inspectAttachment(Attachment at) throws Exception {
	    System.out.println("언제부터 우리는 타인을 믿지 못하게 되었는가? - exiftool없는 허접❤️");
	    File tempFile = File.createTempFile("upload_", ".webp");
	    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
	        fos.write(at.getFileContent());
	    }

	    Metadata metadata = null;
	    try {
	        metadata = ImageMetadataReader.readMetadata(tempFile);
	    } catch (Exception e) {
	        tempFile.delete();
	        return 0; // 분석 실패
	    } finally {
	        tempFile.delete();
	    }

	    StringBuilder metaText = new StringBuilder();
	    boolean hasMake = false, hasModel = false, hasSoftware = false;

	    for (Directory directory : metadata.getDirectories()) {
	        for (Tag tag : directory.getTags()) {
	            String tagStr = (tag.getTagName() + " " + tag.getDescription()).toLowerCase();
	            metaText.append(tagStr).append("\n");
	            // 카메라/소프트웨어 정보
	            if (tagStr.contains("make")) hasMake = true;
	            if (tagStr.contains("model")) hasModel = true;
	            if (tagStr.contains("software")) hasSoftware = true;
	        }
	    }

	    String meta = metaText.toString();

	    //메 타 데 이 터 있 으 라 고
	    if (meta.trim().isEmpty()) {
	        return 2;
	    }

	    //스크린샷? 넌 나가라
	    if (meta.contains("screenshot") || meta.contains("capture") || meta.contains("snipping")) {
	        return 2;
	    }

	    //촬영 정보(제조사/모델/소프트웨어)가 모두 없으면 직찍이 아니야
	    if (!hasMake && !hasModel && !hasSoftware) {
	        return 2;
	    }

	    //통과
	    return 1;
	}

}
