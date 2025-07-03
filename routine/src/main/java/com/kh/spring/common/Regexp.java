package com.kh.spring.common;

//정규식 저장소           
public class Regexp {
	//Challenge
	
	//VideoCall
	public static final String VCNAME = "^.{1,30}$";
	public static final String MAXPARTICIPANTS = "^(10|[1-9])$";
	public static final int EXPIRETIME = 3600; //daily.co의 exp, 단위 : 초

}
