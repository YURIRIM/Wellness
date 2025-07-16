package com.kh.spring.challenge.model.service;

import java.util.Base64;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.kh.spring.challenge.model.dao.ChallengeCommentDao;
import com.kh.spring.challenge.model.vo.ChallengeCommentResponse;
import com.kh.spring.challenge.model.vo.SearchComment;

@Service
public class ChallengeCommentServiceImpl implements ChallengeCommentService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeCommentDao dao;
	@Autowired
	private AttachmentService atService;

	@Override
	//댓글 조회
	public ResponseEntity<byte[]> selectComment(Model model, SearchComment sc) throws Exception {
		List<ChallengeCommentResponse> ccs = dao.chalDetailComment(sqlSession,sc);
		
		//대댓글 조회
		for(ChallengeCommentResponse cc : ccs) {
			sc.setCurrentPage(0);//페이징 초기화
			sc.setRecommentTarget(cc.getCommentNo());
			ccs.addAll(dao.chalDetailRecomment(sqlSession,sc));
		}
		
		for(ChallengeCommentResponse cc : ccs) {
			//댓글 작성자 isOpen!="A" 인 사람 빼고 프로필사진 base64하기
			if(!cc.getIsOpen().equals("A")) {
				byte[] picture = cc.getPicture();
				if(picture!=null) {
					cc.setPictureBase64(Base64.getEncoder().encodeToString(picture));
				}
			}else cc.setNick(null); //A이면 nick도 말소
			cc.setPicture(null);
			
			//댓글 상태가 D면 D져.
			if(cc.getStatus().equals("D")) {
				ChallengeCommentResponse newCc = ChallengeCommentResponse.builder()
						.chalNo(cc.getChalNo())
						.commentNo(cc.getCommentNo())
						.status("D")
						.build();
				cc = newCc;
			}
		}
		
		model.addAttribute("chalDetailComment", ccs);
		model.addAttribute("currentPage", sc.getCurrentPage());
		
		//댓글 사진 조회해서 반납
		return atService.selectAtComment(ccs);
	}
	
	
	@Override
	//대댓글 조회
	public ResponseEntity<byte[]> selectRecomment(Model model, SearchComment sc) throws Exception {
		List<ChallengeCommentResponse> ccs = dao.chalDetailRecomment(sqlSession,sc);

		for(ChallengeCommentResponse cc : ccs) {
			//댓글 작성자 isOpen!="A" 인 사람 빼고 프로필사진 base64하기
			if(!cc.getIsOpen().equals("A")) {
				byte[] picture = cc.getPicture();
				if(picture!=null) {
					cc.setPictureBase64(Base64.getEncoder().encodeToString(picture));
				}
			}else cc.setNick(null); //A이면 nick도 말소
			cc.setPicture(null);
			
			//상태가 D졌어? 그럼 죽어.
			if(cc.getStatus().equals("D")) {
				ChallengeCommentResponse newCc = ChallengeCommentResponse.builder()
						.chalNo(cc.getChalNo())
						.commentNo(cc.getCommentNo())
						.recommentTarget(cc.getRecommentTarget())
						.status("D")
						.build();
				cc = newCc;
			}
		}
		
		model.addAttribute("chalDetailComment", ccs);
		return atService.selectAtComment(ccs);
	}
}
