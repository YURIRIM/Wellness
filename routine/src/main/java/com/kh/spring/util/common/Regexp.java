package com.kh.spring.util.common;

//정규식 저장소           
public class Regexp {
	//Challenge
	public static final String CHAL_TITLE = "^[\\p{L}\\p{N}\\p{M}\\p{S}\\p{P}\\p{Zs}]{1,100}$";
	public static final String CHAL_CONTENT = "^[\\s\\S]{1,1000}$";
	public static final String CHAL_PICTURE_REQUIRED = "^[IYON]$";
	public static final String CHAL_REPLY_REQUIRED = "^[YON]$";
	
	//challenge content 중에서 이미지 링크 연결하기
	public static final String CHAL_CONTENT_ATTACHMENT =
		    "/attachment/select\\?at=([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})";
	
	//페이지당 노출될 챌린지 게시글 수
	public static final int CHAL_SHOW_LIMIT = 20;
	
	//페이지당 노출될 챌린지 댓글 수
	public static final int COMMENT_SHOW_LIMIT = 10;
	
	//SearchChallenge
	public static final String SC_ORDERBY = "^[PS]?$";
	public static final String SC_SEARCH = "^.{0,100}$";
	public static final String SC_SEARCH_TYPE = "^[TCNOJ]?$";
	public static final String SC_STATUS = "^[YN]?$";
	public static final String SC_PICTURE_REQUIRED = "^[IYON]?$";
	public static final String SC_REPLY_REQUIRED = "^[YON]?$";
	public static final int TITLE_SHOW_LIMIT = 20; //제목 글자 표시 제한
	public static final int CONTENT_SHOW_LIMIT = 100; //내용 글자 표시 제한
	
	//searchMyChal
	public static final String SEARCH_MY_CHAL = "^[OJ]?$";
	
	//ChallengeComment
	public static final String CHAL_COMMENT_REPLY = "^[\\s\\S]{0,1000}$";;
	
	//ChalParticipation
	public static final String UPDATE_PARTICIPANT = "^[SF]?$";
	
	//Attachment
	//모든 언어의 글자, 숫자, 언더스코어, 마침표, 하이픈 허가
	public static final String ATTACHMENT_FILE_NAME = "^[\\p{L}\\p{N}._\\-]{1,100}$";
	public static final long MAX_ATTACHMENT_FILE_SIZE = 200 * 1024;
	public static final int ATTACHMENT_FILE_HEIGHT = 1200;
	public static final int ATTACHMENT_FILE_WIDTH = 1200;
	public static final int AT_RESIZE_MAX_LOOP = 5;
	
	//profile
	public static final String PROFIEL_BIO = "^[\\s\\S]{0,100}$";
	public static final String PROFILE_IS_OPEN = "^[YNA]$";
	public static final String PROFILE_WATERMARK_TYPE = "^[DCN]$";
	
	//ai
	public static final int REDUCE_DIMENSION = 128;
	
	//VideoCall
	public static final String VCNAME = "^.{1,30}$";
	public static final String MAXPARTICIPANTS = "^(10|[1-9])$";
	public static final int EXPIRETIME = 3600; //daily.co의 exp, 단위 : 초
	public static final double AT_RESIZE_SHRINKRAT = 0.8;

}
