package com.kh.spring.util.attachment;

import java.io.ByteArrayInputStream;
import java.util.Objects;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.kh.spring.challenge.model.vo.Attachment;

public class ReadMetadata {
    public static int inspectAttachment(Attachment at) throws Exception {
        Metadata metadata;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(at.getFileContent())) {
            metadata = ImageMetadataReader.readMetadata(bais);
        } catch (Exception e) {
            return 0; // 분석 실패
        }

        //Make, Model, Software 태그 유무 확인
        ExifIFD0Directory exifDir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        boolean hasMake     = exifDir != null && exifDir.containsTag(ExifIFD0Directory.TAG_MAKE);
        boolean hasModel    = exifDir != null && exifDir.containsTag(ExifIFD0Directory.TAG_MODEL);
        boolean hasSoftware = exifDir != null && exifDir.containsTag(ExifIFD0Directory.TAG_SOFTWARE);
        
        //전체 메타데이터 텍스트 수집
        StringBuilder sb = new StringBuilder();
        for (Directory dir : metadata.getDirectories()) {
            for (Tag tag : dir.getTags()) {
                String desc = Objects.toString(tag.getDescription(), "").toLowerCase();
                sb.append(desc).append(" ");
            }
        }
        String metaText = sb.toString().trim();
        
        //메타데이터가 없으면 실패
        if (metaText.isEmpty()) {
            return 2;
        }

        //스크린샷 키워드가 있으면 실패
        if (metaText.contains("screenshot")
         || metaText.contains("capture")
         || metaText.contains("snipping")) {
            return 2;
        }

        //Make + Model 정보가 모두 있으면 통과
        if (hasMake && hasModel) {
            return 1;
        }

        return 2;
    }
}
