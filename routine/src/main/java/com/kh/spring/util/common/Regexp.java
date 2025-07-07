package com.kh.spring.util.common;

//정규식 저장소           
public class Regexp {
	//Challenge
	public static final String CHAL_TITLE = "^.{1,100}$";
	public static final String CHAL_CONTENT = "^.{0,1000}$";
	public static final String CHAL_PICTURE_REQUIRED = "^[IYON]$";
	public static final String CHAL_REPLY_REQUIRED = "^[YON]$";
	
	//페이지당 노출될 챌린지 게시글 수
	public static final int CHAL_SHOW_LIMIT = 20;
	
	
	//Attachment
	//모든 언어의 글자, 숫자, 언더스코어, 마침표, 하이픈 허가
	public static final String ATTACHMENT_FILE_NAME = "^[\\p{L}\\p{N}._\\-]{1,100}$";
	public static final long MAX_ATTACHMENT_FILE_SIZE = 200 * 1024;
	public static final int ATTACHMENT_FILE_HEIGHT = 720;
	public static final int ATTACHMENT_FILE_WIDTH = 1280;
	
	//VideoCall
	public static final String VCNAME = "^.{1,30}$";
	public static final String MAXPARTICIPANTS = "^(10|[1-9])$";
	public static final int EXPIRETIME = 3600; //daily.co의 exp, 단위 : 초

}
