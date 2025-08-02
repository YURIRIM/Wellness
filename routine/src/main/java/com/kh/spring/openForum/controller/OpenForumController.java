package com.kh.spring.openForum.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.comments.model.vo.Comments;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.openForum.model.service.OpenForumService;
import com.kh.spring.openForum.model.vo.OpenForum;
import com.kh.spring.openForum.model.vo.OpenForumAttachment;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/openForum")
public class OpenForumController {

	@Autowired
	private OpenForumService service;
	// 자유게시판 기능

	@Value("${file.upload.path}")
	private String filePath;

	// 게시물 리스트 페이지로 이등 메소드
	@GetMapping("/list")
	public String opemForumList(@RequestParam(value = "page", defaultValue = "1") int currentPage, Model model) {

		int listCount = service.listCount(); // 총 게시글 개수
		int boardLimit = 5; // 게시글 보여줄 갯수
		int pageLimit = 8; // 페이징바 개수

		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);

		// 게시글 목록 조회
		ArrayList<OpenForum> posts = service.openForumList(pi);

		model.addAttribute("posts", posts);
		model.addAttribute("pi", pi);
		return "openforum/openForum-list";
	}

	// 1. 게시물 작성 페이지로 이등 메소드
	@GetMapping("/write")
	public String opemForumWrite() {
		return "openforum/openForum-write";
	}

	// 2. 게시물 작성 메소드
	// 작성 후 목록으로 돌아가기
	@PostMapping("/write")
	public String openForumWrite(OpenForum forum, List<MultipartFile> uploadFiles, HttpSession session) {
		ArrayList<OpenForumAttachment> atList = new ArrayList<>();// 첨부파일 정보들 등록할 리스트

		int count = 1;

		for (MultipartFile m : uploadFiles) {
			if (!m.getOriginalFilename().equals("")) {
				String changeName = saveFile(m, session); // 업로드 및 파일명 반환받기
				String originName = m.getOriginalFilename(); // 원본 파일명 추출
				OpenForumAttachment forumAt = new OpenForumAttachment();
				forumAt.setChangeName(changeName);
				forumAt.setOriginName(originName);
				forumAt.setFilePath("/uploadFiles/");

				if (count == 1) {
					forumAt.setFileLevel(count++);
				} else {
					forumAt.setFileLevel(2);
				}
				atList.add(forumAt);
			}
		}

		// 업로드된 파일 정보를 담아주는 객체 MultipartFile
		// 요청시 input file 태그의 name값과 일치하도록 참조변수명 설정하기
		// 첨부파일이 전달되었는지 확인하기
		// 전달된 파일이 있을 경우 파일명이 빈문자열이 아닌 경우일테니 판별용 조건으로 사용
		// 전달된 파일이 없어도 MultipartFile에는 객체가 생성되어 주입되기 때문에 null값 비교로 판별할 수 없음.

		// 파일명 추출 메소드 : getOriginalFileName()
		// 파일명이 빈문자열이 아닌경우 (전달된 파일이 있는 경우)

		/*
		 * if(!uploadFile.getOriginalFilename().equals("")) { String changeName =
		 * saveFile(uploadFile,session);
		 * 
		 * //forum 객체에 원본파일명과 변경된 파일명을 담아주기
		 * forum.setOriginName(uploadFile.getOriginalFilename());
		 * forum.setChangeName("/uploadFiles/"+changeName); }
		 */
		int result = service.openForumWrite(forum, atList);

		if (result > 0) {
			session.setAttribute("alertMsg", "게시글 작성 성공!");
		} else {
			session.setAttribute("alertMsg", "게시글 작성 오류 발생!"); // alertMsg 나오게 view 페이지에 뭐 만들어야함
		}

		return "redirect:/openForum/list"; // 게시글 목록페이지로 재요청
	}

	// 3. 게시물 상세보기 메소드
	@GetMapping("/detail")
	public String postDetail(int postId, Model model) {
		// postId(글번호) 이용해서 조회수 증가 및 게시글 조회
		int result = service.increaseCount(postId);

		if (result > 0) {
			OpenForum post = service.postDetail(postId);
			model.addAttribute("post", post);
			return "openforum/openForum-detail";
		} else {
			model.addAttribute("errorMsg", "오류발생!");
			return "common/errorPage";
		}
	}

	// 파일 업로드 처리 메소드
	public String saveFile(MultipartFile uploadFile, HttpSession session) {

		String originName = uploadFile.getOriginalFilename(); // 1. 원본 파일명 추출
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // 2. 시간 형식 문자열로 추출
		int ranNum = (int) (Math.random() * 90000 + 10000); // 3. 랜덤값 자리 추출
		String ext = originName.substring(originName.lastIndexOf(".")); // 4.원본파일에서 확장자 추출
		String changeName = currentTime + ranNum + ext; // 5. 합치기
		// String savePath =
		// session.getServletContext().getRealPath("/resources/uploadFiles/"); //6.서버에
		// 업로드 처리할 때 물리적인 경로 추출하기

		// 7. 경로와 변경된 이름을 이용하여 파일 업로드 처리 메소드 수행: MultipartFile 의 transferTo() 메소드
		try {
			uploadFile.transferTo(new File(filePath + changeName));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return changeName;
	}

	// AJAX를 통해 첨부파일을 각각 삭제 - 데이터베이스에서 해당 첨부파일 레코드를 삭제하고, 서버의 실제 파일도 삭제합니다.
	// @param fileId 삭제할 첨부파일의 ID
	// @param session HttpSession (파일 경로 획득용)
	// @return 성공 시 1, 실패 시 0 반환 (JSON 형태로 클라이언트에 전달)

	@PostMapping("/delete-attachment") // HTML의 AJAX `url`과 일치
	@ResponseBody // 이 어노테이션을 통해 반환 값(int)이 HTTP 응답 본문에 직접 쓰여집니다.
	public int deleteAttachment(@RequestParam("fileId") int fileId, HttpSession session) {
		System.out.println(fileId);
		System.out.println("왜이렇게안될까요?");

		
		int result = 0; // 초기 결과는 실패 (0)

		// 1. 데이터베이스에서 첨부파일 정보 조회 (서버 파일 삭제를 위해 changeName 필요)
		OpenForumAttachment attachmentToDelete = service.selectAttachmentByFileId(fileId);

		if (attachmentToDelete != null) {
			// 지울 첨부파일
			// 2. 데이터베이스에서 첨부파일 레코드 삭제
			// 이 메소드는 DB에서 해당 fileId의 레코드를 삭제하고, 성공하면 1을 반환해야 합니다.

			result = service.deleteAttachment(fileId); // mapper 작성구문: DB 삭제를 담당

			// 3. DB 삭제 성공 시, 서버의 실제 파일도 삭제
			if (result > 0) {
				String changeName = attachmentToDelete.getChangeName();
				if (changeName != null) {
					File fileOnServer = new File(filePath + changeName);
					if (fileOnServer.exists()) {
						if (fileOnServer.delete()) {
							System.out.println("서버 파일 삭제 성공: " + changeName);
						} else {
							System.err.println("서버 파일 삭제 실패: " + changeName);
							// 로그를 남기거나, 사용자에게 알릴 수 있는 다른 조치
						}
					} else {
						System.out.println("삭제할 서버 파일이 존재하지 않습니다: " + changeName);
					}
				}
			} else {
				System.err.println("DB 첨부파일 삭제 실패 (fileId: " + fileId + ")");
			}
		} else {
			System.err.println("삭제할 첨부파일 정보를 찾을 수 없습니다 (fileId: " + fileId + ")");
		}

		return result; // 클라이언트(AJAX success 함수)로 결과 반환
	}

	// 댓글(Comments) 작성
	@ResponseBody
	@PostMapping("/write-comments")
	public int writeComments(Comments c) {
		int result = service.writeComments(c);
		return result;
	}

	// 댓글 리스트 조회 (CommentList)
	@ResponseBody // 데이터 자체 리턴
	@GetMapping(value = "/comments-list", produces = "application/json;charset=UTF-8")
	public ArrayList<Comments> commentList(int postId) {
		ArrayList<Comments> cList = service.commentList(postId);

		return cList;
	}

	// 4. 게시물 삭제
	@PostMapping("/delete")
	public String deletePost(int postId, @RequestParam(required = false) List<String> filePaths, HttpSession session) { // OpenForum

		// 게시글 삭제 처리
		int result = service.deletePost(postId);

		if (result > 0) {
			session.setAttribute("alertMsg", "게시글 삭제 성공");

			// 첨부파일이 있는 경우 삭제 처리
			if (filePaths != null && !filePaths.isEmpty()) {
				for (String changeName : filePaths) {
					File file = new File(this.filePath + changeName);
					if (file.exists()) {
						file.delete();
					}
				}
			}

			// 삭제 성공 후 목록 페이지로 리다이렉트
			return "redirect:/openForum/list";
		} else {
			session.setAttribute("alertMsg", "게시글 삭제 실패");
			return "redirect:/openForum/detail/" + postId;
		}
	}

	// 5. 게시물 수정 페이지로 이동
	@GetMapping("/update/{postId}")
	public String updatePost(@PathVariable("postId") int postId, Model model) {
		OpenForum post = service.postDetail(postId); // 글번호로 게시물 정보 조회
		model.addAttribute("post", post); // 위임하는 페이지에 게시글 정보 담아가기

		return "openforum/postUpdateForm";
	}

	// 6. 게시물 수정
	// 게시물이 여러개임을 잊지 말자 ㅠㅠ
	@PostMapping("/modify-post")
	public String modifyPost(OpenForum post, List<MultipartFile> newFiles, HttpSession session) {

		int postId = post.getPostId();

		// 파일명이 있을때(새로운 업로드파일이 전달되었을때)
		if (!newFiles.isEmpty() && newFiles != null) {

			ArrayList<OpenForumAttachment> newList = new ArrayList<>();

			for (MultipartFile file : newFiles) {
				if (!file.isEmpty()) {
					String changeName = saveFile(file, session);

					OpenForumAttachment att = new OpenForumAttachment();
					att.setOriginName(file.getOriginalFilename());
					att.setChangeName(changeName);
					att.setFilePath("/uploadFiles/");

					newList.add(att);
				}
			}

			// atList 에 파일 리스트 저장
			post.setAtList(newList);

			int result = service.modifyPost(post, newList);
			// 정보수정 성공시 성공메시지와 함께 기존에 첨부파일이 있었다면 삭제 후 디테일뷰로 재요청
			if (result > 0) {
				session.setAttribute("alertMsg", "게시글 수정 성공!");

			} else {
				// 정보수정 실패시 실패 메시지와 함께 디테일뷰로 재요청
				session.setAttribute("alertMsg", "게시글 수정 실패!");

			}
			return "redirect:/openForum/detail?postId=" + postId;
		} else {

			int result = service.modifyPost(post, null);

			if (result > 0) {
				session.setAttribute("alertMsg", "게시글 수정 성공!");
			} else {
				session.setAttribute("alertMsg", "게시글 수정 실패!");
			}

			return "redirect:/openForum/detail?postId=" + postId;
		}
	}
	
	// 7. 댓글 삭제

	// 0. sorting, 검색, 파일 업로드, 조회수 증가 메소드

}  

