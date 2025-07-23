package com.kh.spring.challenge.model.service;

import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.dao.ChalParticipationDao;
import com.kh.spring.challenge.model.dao.ChallengeCategoryDao;
import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.ConnectByUuid;
import com.kh.spring.challenge.model.vo.LoginUserAndChal;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeFix;
import com.kh.spring.util.challenge.ChallengeValidator;
import com.kh.spring.util.challenge.SearchChallengeValidator;
import com.kh.spring.util.common.BinaryAndBase64;
import com.kh.spring.util.common.Dummy;
import com.kh.spring.util.common.Regexp;
import com.kh.spring.util.common.UuidUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class ChallengeServiceImpl implements ChallengeService {
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeDao chalDao;
	@Autowired
	private AttachmentDao atDao;

	@Autowired
	private ChalParticipationDao partiDao;

	//챌린지 리스트 조회
	@Override
	public ResponseEntity<List<ChallengeResponse>> selectChal(HttpSession session
			, Model model, SearchChallenge sc) throws Exception {
		//sc 유효성 확인 및 null을 빈 문자열로 변환(날짜 필드 제외)
		if (!SearchChallengeValidator.searchChallenge(sc))
			throw new Exception("searchChallenge 유효성");
		
		//searchType이 내가 생성 및 참여한 챌린지 이면 그쪽 메소드로 가세요라
		if(sc.getSearchType().equals("O")||sc.getSearchType().equals("J")) {
			SearchMyChallenge smc = SearchMyChallenge.builder()
					.currentPage(sc.getCurrentPage())
					.searchType(sc.getSearchType())
					.build();
			return selectMyChal(session,model,smc);
		}
		
		model.addAttribute("searchKeyword", sc);//모달에 검색어 저장
		
		//search trim()
		sc.setSearch(sc.getSearch().trim());
		
		// DB에서 페이지 긁어오기
		List<ChallengeResponse> result = chalDao.selectChal(sqlSession,sc);
		
		//비었으면 404
		if (result == null || result.isEmpty())
			return ResponseEntity.notFound().build();

		challengeResponseSanitizer(result);
		
		return ResponseEntity.ok()
				.header("Content-Type", "application/json; charset=UTF-8")
				.body(result);
	}
	
	//내가 생성 및 참여한 챌린지 조회하기
	@Override
	public ResponseEntity<List<ChallengeResponse>> selectMyChal(HttpSession session
			, Model model, SearchMyChallenge smc) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		smc.setUserNo(loginUser.getUserNo());
		
		//smc 유효성 확인
		if (!SearchChallengeValidator.searchMyChallenge(smc))
			throw new Exception("searchMyChallenge 유효성");
		
		model.addAttribute("searchKeyword", smc);//모달에 검색어 저장
		
		// DB에서 페이지 긁어오기
		List<ChallengeResponse> result = chalDao.selectMyChal(sqlSession,smc);
		
		//비었으면 404
		if (result == null || result.isEmpty())
			return ResponseEntity.notFound().build();
		
		challengeResponseSanitizer(result);
		
		return ResponseEntity.ok()
				.header("Content-Type", "application/json; charset=UTF-8")
				.body(result);
	}

	// 챌린지 생성하기
	@Override
	@Transactional
	public void newChal(HttpSession session, Model model, ChallengeRequest chal) throws Exception {
		User user = (User) session.getAttribute("loginUser");
		if (user == null)
			user = Dummy.dummyUser(); // 로그인되지 않았으면 더미데이터
		
		// 제목 trim
		chal.setTitle(chal.getTitle().trim());

		// challenge 유효성 확인
		if (!ChallengeValidator.challenge(chal))
			throw new Exception("challenge 유효성");

		// 썸네일 있으면 Base64 url-safe 디코딩, 리사이즈 및 소독
		byte[] thumbnail = BinaryAndBase64.base64ToBinary(chal.getThumbnailBase64());
		if (thumbnail !=null) {
			//썸네일 있으면 thumbnail에 넣기
			chal.setThumbnail(thumbnail);
			chal.setThumbnailBase64(null);
		}

		// DB저장
		chal.setUserNo(user.getUserNo());
		int result = chalDao.newChal(sqlSession, chal);

		// 실패하면 가세요라
		if (!(result > 0))
			throw new Exception("chall DB저장 실패");

		//------------------첨부사진 나와바리------------------
		//이미지 uuid 목록 추출
		Pattern pattern = Pattern.compile(Regexp.CHAL_CONTENT_ATTACHMENT);
		Matcher matcher = pattern.matcher(chal.getContent());
		
		while (matcher.find()) {
			//uuid로 모든 사진 연결하기
			String uuidStr = matcher.group(1);
			ConnectByUuid cbu = ConnectByUuid.builder()
					.refNo(chal.getChalNo())
					.uuid(UuidUtil.strToByteArr(uuidStr))
					.build();
			result = atDao.connectAtbyUuid(sqlSession,cbu);
			
			if (!(result>0)) throw new Exception("사진 연결 실패");
		}
		
		
	}
	
	//챌린지 세부보기
	@Override
	public void chalDetail(HttpServletRequest request, HttpSession session
			,Model model, int chalNo) throws Exception {
		ChallengeResponse chal = chalDao.chalDetail(sqlSession,chalNo);
		
		//이 챌린지에 현재 로그인한 유저가 참여중임?
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) chal.setLoginUserIsParticipation("U");
		else {
			LoginUserAndChal lac = LoginUserAndChal.builder()
					.chalNo(chalNo)
					.userNo(loginUser.getUserNo())
					.status("notOnlyY")
					.build();
			String result = partiDao.loginUserIsParticipant(sqlSession,lac);
			if(result==null) result = "N";
			chal.setLoginUserIsParticipation(result);
		}
		
		//챌린지 작성자가 익명일 경우 비워부려
		if(chal.getIsOpen().equals("A")) {
			chal.setPicture(null);
			chal.setBio(null);
			chal.setNick(null);
		}
		
		//썸네일 base64인코딩
		byte[] thumbnail = chal.getThumbnail();
		if(thumbnail!=null) {
			chal.setThumbnailBase64(Base64.getEncoder().encodeToString(thumbnail));
			chal.setThumbnail(null);
		}
		//프로필 사진 정상화
		byte[] picture = chal.getPicture();
		if(picture!=null) {
			chal.setPictureBase64(Base64.getEncoder().encodeToString(picture));
			chal.setPicture(null);
		}
		
		//content의 img태그 링크 정상화 및 스크립트 주석처리
		chal.setContent(ChallengeFix.fixScrAndScript(chal.getContent(), request));
		
		//verifyCycle -> verifyCycleStr
		chal.setVerifyCycleStr(ChallengeFix.verifyCycleStr(chal.getVerifyCycle()));
		
		model.addAttribute("chalDetail", chal);
	}

	//챌린지 수정 페이지로
	@Override
	public void goUpdateChal(Model model, int chalNo) throws Exception{
		//여기서 별도 유효성 검사는 하지 않음. 수정해서 요청할 때 검사하기
		
		//챌린지를 받아서 모달에 담아 넘기기
		//요청용 객체에 담자
		ChallengeRequest chal = chalDao.goUpdateChal(sqlSession,chalNo);
		
		byte[] thumbnail = chal.getThumbnail();
		if(thumbnail!=null) {
			chal.setThumbnailBase64(Base64.getEncoder().encodeToString(thumbnail));
			chal.setThumbnail(null);
		}
		
		model.addAttribute("updateChal",chal);
	}
	
	//챌린지 수정
	@Override
	public ResponseEntity<Void> updateChal(HttpSession session, ChallengeRequest chal) throws Exception {
		//수정 권한 확인
		User loginUser = (User)session.getAttribute("loginUser");
		LoginUserAndChal lac = LoginUserAndChal.builder()
				.userNo(loginUser.getUserNo())
				.chalNo(chal.getChalNo())
				.build();
		int loginUserIsWriter = chalDao.loginUserIsWriter(sqlSession,lac);
		if(!(loginUserIsWriter>0)) return ResponseEntity.badRequest().build();
		
		//뭐뭣 사진 요구사항이 비었다고? 프론트 일 안 하냐?
		if(chal.getPictureRequired()==null) chal.setPictureRequired("I");
		
		if(!ChallengeValidator.challengeUpdate(chal)) return ResponseEntity.badRequest().build();
		
		// 제목 trim
		chal.setTitle(chal.getTitle().trim());
		
		// 썸네일 있으면 Base64 url-safe 디코딩, 리사이즈 및 소독
		byte[] thumbnail = BinaryAndBase64.base64ToBinary(chal.getThumbnailBase64());
		if (thumbnail !=null) {
			//썸네일 있으면 thumbnail에 넣기
			chal.setThumbnail(thumbnail);
			chal.setThumbnailBase64(null);
		}
		
		//수정하기
		chal.setUserNo(loginUser.getUserNo());
		int result = chalDao.updateChal(sqlSession,chal);
		if(!(result>0)) return ResponseEntity.status(500).build();
		
		//------------------첨부사진 나와바리------------------
		//이미지 uuid 목록 추출
		Pattern pattern = Pattern.compile(Regexp.CHAL_CONTENT_ATTACHMENT);
		Matcher matcher = pattern.matcher(chal.getContent());
		
		while (matcher.find()) {
			//uuid로 모든 사진 연결하기
			String uuidStr = matcher.group(1);
			ConnectByUuid cbu = ConnectByUuid.builder()
					.refNo(chal.getChalNo())
					.uuid(UuidUtil.strToByteArr(uuidStr))
					.build();
			result = atDao.connectAtbyUuid(sqlSession,cbu);
			
			if (!(result>0)) return ResponseEntity.status(500).build();
		}
		
		
		return ResponseEntity.ok().build();
	}
	

	//비동기 - 챌린지 삭제
	@Override
	public void deleteChal(HttpSession session, int chalNo) throws Exception{
		//권한 확인
		User loginUser = (User)session.getAttribute("loginUser");
		LoginUserAndChal lac = LoginUserAndChal.builder()
				.userNo(loginUser.getUserNo())
				.chalNo(chalNo)
				.build();
		int loginUserIsWriter = chalDao.loginUserIsWriter(sqlSession,lac);
		
		if(!(loginUserIsWriter>0)) throw new Exception("접근 권한이 없습니다.");
		
		//삭제하기
		int result = chalDao.deleteChal(sqlSession,chalNo);
		if(!(result>0)) throw new Exception("챌린지 삭제 실패");
	}
	
	//비동기 - 챌린지 종료
	@Override
	public void closeChal(HttpSession session, int chalNo) throws Exception {
		//권한 확인
		User loginUser = (User)session.getAttribute("loginUser");
		LoginUserAndChal lac = LoginUserAndChal.builder()
				.userNo(loginUser.getUserNo())
				.chalNo(chalNo)
				.build();
		int loginUserIsWriter = chalDao.loginUserIsWriter(sqlSession,lac);
		
		if(!(loginUserIsWriter>0)) throw new Exception("접근 권한이 없습니다.");
		
		//종료하기
		int result = chalDao.closeChal(sqlSession,chalNo);
		if(!(result>0)) throw new Exception("챌린지 종료 실패");
	}
	
	
	//조회한 챌린지 리스트 소독하기
	@Override
	public void challengeResponseSanitizer(List<ChallengeResponse> result) throws Exception{
		for (ChallengeResponse chal : result) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			
			//내용에 이미지 태그 등이 있을 경우
			chal.setContent(ChallengeFix.deleteContentTag(chal.getContent()));
			
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
			}
			
			//verifyCycleStr 정상화
			chal.setVerifyCycleStr(ChallengeFix.verifyCycleStr(chal.getVerifyCycle()));
			
			//썸네일 base64인코딩
			byte[] thumbnail = chal.getThumbnail();
			if(thumbnail!=null) {
				chal.setThumbnailBase64(Base64.getEncoder().encodeToString(thumbnail));
				chal.setThumbnail(null);
			}
			
			//익명일 경우 비활성화
			if(chal.getIsOpen().equals("A")) {
				chal.setPicture(null);
				chal.setNick("익명의 ROUTINE 유저");
				chal.setBio("");
				chal.setUserNo(0);
			}
			
			//프로필 사진 base64인코딩
			byte[] picture = chal.getPicture();
			if(picture!=null) {
				chal.setPictureBase64(Base64.getEncoder().encodeToString(picture));
				chal.setPicture(null);
			}
		}
	}
}
