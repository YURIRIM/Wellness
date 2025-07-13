package com.kh.spring.util.common;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kh.spring.challenge.model.vo.Profile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {
	
	// : 뒤로 있는 부분은 default(해당 키값에 데이터가 없을 시 기본값)
	@Value("${jwt.secret:dksdkwnjdytlfgdmsepdy}")
	private String secret;
	
	@Value("${jwt.expiration:8640000}")
	private long expiration;
	
	//JWT 토큰에 인증을 하기 위한 암호키 생성 메소드
	private SecretKey getSignKey() {
		//secret 문자열을 바이트 배열로 변환해 HMAC키로 변환
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
	
	//자신 프로필 JWT로 저장
	public String userProfileToken(Profile userProfile) {
		Date now = new Date();
		
		Date expiryDate = new Date(now.getTime()+expiration); //현재시간+만료시간
		
		//JWT 토큰 빌더패턴을 이용해 토큰 생성
		return Jwts.builder()
				.setSubject(String.valueOf(userProfile.getUserNo())) //Subject - 토큰 대상
				.setIssuedAt(now) //발급된 때
				.setExpiration(expiryDate) //만료될 때
				.signWith(getSignKey()) //생성된 암호화 키로 토큰에 디지털 서명(토큰 위조 검증)
				.claim("userNo", userProfile.getUserNo()) //추가적으로 토큰에 담을 데이터
				.claim("bio", userProfile.getBio())
				.claim("pictureBase64", userProfile.getPictureBase64())
				.claim("isOpen", userProfile.getIsOpen())
				.claim("chalParticiapteCount", userProfile.getChalParticiapteCount())
				.claim("successCount", userProfile.getSuccessCount())
				.claim("failCount", userProfile.getFailCount())
				.claim("successRatio", userProfile.getSuccessRatio())
				.claim("failRatio", userProfile.getFailRatio())
				.compact(); //최종적으로 JWT문자열 형태로 압축해 반환해주는 메소드
	}
	
	//토큰에서 userId 가져오기
	public String getUserIdFormToken(String token) {
		//jwt파서로 토큰 해독하고 원하는 정보 추출
		Claims claims = Jwts.parserBuilder() //jwt파서 빌더
				.setSigningKey(getSignKey()) //서명 검증
				.build() //객체 생성
				.parseClaimsJws(token) //해당 객체에 jwt토큰 파싱해 검증
				.getBody(); //Claims객체 추출
		return claims.getSubject(); //주체 값 추출(토큰 생성시 setSubject() - 주체에 userId설정됨)
		
	}
	
	//JWT유효한지 확인하는 검증 메소드
	public boolean vaildateToken(String token) {
		try {
			//토큰 파싱 시도 - 성공시 유효한 토큰 / 예외 발생 시 유효하지 않은 토큰
			Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token); //검증용
			
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	/*
	 * 토큰 검증 및 사용자 정보 반환 메소드
	 * 목적 : 앱 시작시 저장된 토큰으로 자동 로그인 처리 + api요청시 토큰 검증하기
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ResponseEntity
	 * [표현법] ResponseEntitiy<Map<String, Object>>
	 * Http 응답 전체 (상태코드, 헤더, 바디)를 합쳐 보내는 형태
	 * 기존 @ResponseBody는 응답형태가 바디만 다룰 수 있기 때문에
	 * 좀 더 유연한 ResponseEntity를 이용하면 상태코드에 따른 처리, 헤더정보 등을 사용할 수 있다.
	 * 
	 * ResponseEntity.ok() : 200
	 * ResponseEntity.ok(map);//200번 상태응답 및 map에 담긴 데이터 전달
	 * 
	 * ResponseEntitiy.badRequest() : 400(Bad Request - 잘못된 요청)
	 * ResponseEntitiy.badRequest().body(map);
	 * 
	 * ResponseEntitiy.internalServerError() : 500
	 * ResponseEntitiy.internalServerError().body(map);
	 * 
	 * JWT방식 토큰으로는 서버에서 처리할 게 없다.
	 * 로그아웃시 프론트에서 토큰 삭제
	 * RestFul요청 형태를 일관성있게 유지하고 토큰에 확장성을 고려해 작성하자
	 * 
	 * 
	 * ----------------------------------------
	 * 컨트롤러에서 http요청의 헤더 추출하기
	 * 
	 * request.getHeader();
	 * 
	 */
}
