package com.kh.spring.qna.model.vo;

import java.util.Date;

import com.kh.spring.user.model.vo.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Qna {

	 private int userQnaNo;//	문의사항번호
	 private int userNo;//	유저번호
	 private String userQnaTitle;//	문의제목
	 private String userQna;//	문의내용
	 private Date createdate;//	작성일
	 private boolean status;//	대답여부
}
