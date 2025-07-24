package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
//챌린지에 사진/리플이 필요한가요?
public class ChallengeRequired {
    private String pictureRequired; //I:직접 촬영한 사진 필수 Y:필수 O:선택 N:불가
    private String replyRequired; //Y:필수 O:선택 N:불가
}
