package com.kh.spring.challenge.model.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.dao.ChallengeCategoryDao;
import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchComment;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeValidator;
import com.kh.spring.util.challenge.SearchChallengeValidator;
import com.kh.spring.util.common.BinaryAndBase64;
import com.kh.spring.util.common.Dummy;
import com.kh.spring.util.common.Regexp;
import com.kh.spring.util.common.UuidUtil;

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
	private ChallengeCategoryDao ccDao;
	@Autowired
	private ChallengeCommentService commentService;

	//controllerAdviser - CC조회
	@Override
	public List<ChallengeCategory> selectCCList() {
		return ccDao.selectCCList(sqlSession);
	}

	//챌린지 리스트 조회
	@Override
	public void selectChal(HttpSession session, Model model, SearchChallenge sc) throws Exception {
		model.addAttribute("searchChallenge", sc);//모달에 검색어 저장

		System.out.println("챌린지 검색어 : "+sc);
		
		//sc 유효성 확인
		if (!SearchChallengeValidator.searchChallenge(sc))
			throw new Exception("searchChallenge 유효성");
		
		//search trim()
		sc.setSearch(sc.getSearch().trim());

		// DB에서 페이지 긁어오기
		List<ChallengeResponse> result = chalDao.selectChal(sqlSession, sc);
		
		if (result == null || result.isEmpty())
			throw new Exception("챌린지 리스트 조회 불가");

		for (ChallengeResponse chal : result) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
			}
			
			
			//썸네일 base64인코딩
			byte[] thumbnail = chal.getThumbnail();
			if(thumbnail!=null) {
				chal.setThumbnailBase64(Base64.getEncoder().encodeToString(thumbnail));
				chal.setThumbnail(null);
			}
			
			//프로필 사진 base64인코딩
			byte[] picture = chal.getPicture();
			if(picture!=null) {
				chal.setPictureBase64(Base64.getEncoder().encodeToString(picture));
				chal.setPicture(null);
			}
		}

		// 모달에 넣기
		model.addAttribute("chalList", result);
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
			byte[] uuid = UuidUtil.strToByteArr(uuidStr);
			
			result = atDao.connectAtbyUuid(sqlSession,uuid);
			
			if (!(result>0)) throw new Exception("사진 연결 실패");
		}
		
		
	}
	
	//내가 참여/생성한 챌린지 보기
	@Override
	public void myChal(HttpSession session, Model model, SearchMyChallenge smc) throws Exception {
		User user = (User) session.getAttribute("loginUser");
		model.addAttribute("searchChallenge", smc);//모달에 검색어 저장

		smc.setUserNo(user.getUserNo());
		System.out.println("챌린지 검색어 : "+smc);
		
		//유효성 검사
		if (!SearchChallengeValidator.searchMyChallenge(smc))
			throw new Exception("searchMyChallenge 유효성");
		
		//유형에 따라 DB에서 긁어오기
		List<ChallengeResponse> result =  chalDao.selectMyChal(sqlSession, smc);
		
		if (result == null || result.isEmpty())
			throw new Exception("챌린지 리스트 조회 불가");

		for (ChallengeResponse chal : result) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
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
		}

		// 모달에 넣기
		model.addAttribute("chalList", result);
	}
	
	//챌린지 세부보기
	@Override
	public void chalDetail(HttpSession session, Model model, int chalNo) throws Exception {
		ChallengeResponse chal = chalDao.chalDetail(sqlSession,chalNo);
		
		//이 챌린지에 현재 로그인한 유저가 참여중임?
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) chal.setLoginUserIsParticipation("U");
		else {
			Map<String, Integer> map = new HashMap<>() {
				//직렬화? 그게 뭐죠?
				private static final long serialVersionUID = 1L;
				{
			    put("userNo", loginUser.getUserNo());
			    put("chalNo", chal.getChalNo());
			}};
			String result = chalDao.loginUserIsParticipant(sqlSession,map);
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
		
		model.addAttribute("chalDetail", chal);
		
		
		//댓글 조회
		SearchComment sc = SearchComment.builder()
				.chalNo(chalNo)
				.currentPage(0)
				.currentRecommentPage(0)
				.build();
		commentService.selectComment(model,sc);
	}

	//비동기 - 챌린지 참여하기
	@Override
	public void chalParticipate(HttpSession session, Model model, int chalNo) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		//너는 참여할 권한이 있니?
		Map<String, Integer> map = new HashMap<>() {
			//직렬...화? 데...쥬레 같은 건가요?
			private static final long serialVersionUID = 1L;
			{
		    put("userNo", loginUser.getUserNo());
		    put("chalNo", chalNo);
		}};
		String state = chalDao.loginUserIsParticipant(sqlSession,map);
		if(state!=null) throw new Exception("이미 참여했잖아...");
		
		int result = chalDao.newParticipant(sqlSession,map);
		if(!(result>0)) throw new Exception("참여 할 수 없네용 까비아깝숑");
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
	public void updateChal(HttpSession session, Model model, ChallengeRequest chal) throws Exception {
		
	}
	

	//비동기 - 챌린지 삭제
	@Override
	public void deleteChal(HttpSession session, int chalNo) throws Exception{
		//권한 확인
		User loginUser = (User)session.getAttribute("loginUser");
		Map<String, Integer> map = new HashMap<>() {
			private static final long serialVersionUID = 1L;
			{
		    put("userNo", loginUser.getUserNo());
		    put("chalNo", chalNo);
		}};
		int loginUserIsWriter = chalDao.loginUserIsWriter(sqlSession,map);
		
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
		Map<String, Integer> map = new HashMap<>() {
			private static final long serialVersionUID = 1L;
			{
		    put("userNo", loginUser.getUserNo());
		    put("chalNo", chalNo);
		}};
		int loginUserIsWriter = chalDao.loginUserIsWriter(sqlSession,map);
		
		if(!(loginUserIsWriter>0)) throw new Exception("접근 권한이 없습니다.");
		
		//종료하기
		int result = chalDao.closeChal(sqlSession,chalNo);
		if(!(result>0)) throw new Exception("챌린지 종료 실패");
	}
}
