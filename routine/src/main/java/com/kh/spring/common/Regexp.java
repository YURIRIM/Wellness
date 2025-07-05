package com.kh.spring.common;

//정규식 저장소           
public class Regexp {
	//Challenge
	public static final String CHAL_TITLE = "^.{1,100}$";
	public static final String CHAL_CONTENT = "^.{0,1000}$";
	public static final String CHAL_VERIFY_CYCLE = "^(10|[1-9])$";
	public static final String CHAL_PICTURE_REQUIRED = "^[YON]$";
	public static final String CHAL_REPLY_REQUIRED = "^[YON]$";
	
	//Attachment
	public static final String ATTACHMENT_FILE_NAME = "^.{1,100}$";
	public static final long MAX_ATTACHMENT_FILE_SIZE = 5 * 1024 * 1024;
	
	
	//VideoCall
	public static final String VCNAME = "^.{1,30}$";
	public static final String MAXPARTICIPANTS = "^(10|[1-9])$";
	public static final int EXPIRETIME = 3600; //daily.co의 exp, 단위 : 초

}
