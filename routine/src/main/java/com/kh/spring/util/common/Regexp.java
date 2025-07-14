package com.kh.spring.util.common;

import java.time.LocalDate;

//정규식 저장소           
public class Regexp {
	//Challenge
	public static final String CHAL_TITLE = "^.{1,100}$";
	public static final String CHAL_CONTENT = "^.{1,1000}$";
	public static final String CHAL_PICTURE_REQUIRED = "^[IYON]$";
	public static final String CHAL_REPLY_REQUIRED = "^[YON]$";
	public static final LocalDate DOWNFALL = LocalDate.of(1453, 5, 29);
	
	//페이지당 노출될 챌린지 게시글 수
	public static final int CHAL_SHOW_LIMIT = 20;
	
	//페이지당 노출될 챌린지 댓글 수
	public static final int COMMENT_SHOW_LIMIT = 20;
	
	//SearchChallenge
	public static final String SC_ORDERBY = "^[PS]?$";
	public static final String SC_SEARCH = "^.{0,100}$";
	public static final String SC_SEARCH_TYPE = "^[TCN]?$";
	public static final String SC_STATUS = "^[YN]?$";
	public static final String SC_PICTURE_REQUIRED = "^[IYON]?$";
	public static final String SC_REPLY_REQUIRED = "^[YON]?$";
	public static final int TITLE_SHOW_LIMIT = 20; //제목 글자 표시 제한
	public static final int CONTENT_SHOW_LIMIT = 100; //내용 글자 표시 제한
	
	//SearchMyChallenge
	public static final String SMC_SEARCH_TYPE = "^[OJ]?$";
	
	
	//Attachment
	//모든 언어의 글자, 숫자, 언더스코어, 마침표, 하이픈 허가
	public static final String ATTACHMENT_FILE_NAME = "^[\\p{L}\\p{N}._\\-]{1,100}$";
	public static final long MAX_ATTACHMENT_FILE_SIZE = 200 * 1024;
	public static final int ATTACHMENT_FILE_HEIGHT = 1200;
	public static final int ATTACHMENT_FILE_WIDTH = 1200;
	
	//profile
	public static final String PROFIEL_BIO = "^.{0,100}$";
	public static final String PROFILE_IS_OPEN = "^[YNA]$";
	public static final String PROFILE_WATERMARK_TYPE = "^[DFN]$";
	
	
	//VideoCall
	public static final String VCNAME = "^.{1,30}$";
	public static final String MAXPARTICIPANTS = "^(10|[1-9])$";
	public static final int EXPIRETIME = 3600; //daily.co의 exp, 단위 : 초

}
