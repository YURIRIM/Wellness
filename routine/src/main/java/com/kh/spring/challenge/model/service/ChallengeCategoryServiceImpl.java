package com.kh.spring.challenge.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.kh.spring.ai.service.GeminiEmbeddingService;
import com.kh.spring.challenge.model.dao.ChallengeCategoryDao;
import com.kh.spring.challenge.model.vo.CategorySimilarity;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.util.ai.LoadCategoryExplanation;
import com.kh.spring.util.ai.VectorUtil;
import com.kh.spring.util.challenge.ChallengeFix;
import com.kh.spring.util.common.Regexp;

@Service
public class ChallengeCategoryServiceImpl implements ChallengeCategoryService {
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeCategoryDao dao;
	@Autowired
	private GeminiEmbeddingService geminiService;

	// controllerAdviser - CC조회
	@Override
	public List<ChallengeCategory> selectCCList() throws Exception {
		return dao.selectCCList(sqlSession);
	}

	// 비동기 - 카테고리 임베딩 해놓기
	@Override
	@Transactional
	public ResponseEntity<Void> categoryToVector() throws Exception {
		int result = 1;
		List<ChallengeCategory> explantions = LoadCategoryExplanation.getExplanation();

		for (ChallengeCategory cce : explantions) {

			// 벡터화, 랜덤 투형 및 1xd로 변경, json으로 만들기
			float[] vectors = geminiService.getEmbedding(cce.getEmbedding());
			vectors = VectorUtil.randomProject(vectors);
			float[][] vectors2D = {vectors};
			String json = new Gson().toJson(vectors2D);
			
			if (json == null)
				return ResponseEntity.status(500).build();

			cce.setEmbedding(json);
			result *= dao.updateEmbedding(sqlSession, cce);
		}

		if (!(result > 0))
			return ResponseEntity.status(500).build();

		return ResponseEntity.ok().build();
	}

	// 비동기 - 카테고리 추천 알고리즘
	@Override
	public ResponseEntity<List<Integer>> recommend(String title, String content) throws Exception {
		if(title==null) title="";
		if(content==null) content="";
		
		//HTML 태그 소각
		title = ChallengeFix.deleteContentTag(title);
		content = ChallengeFix.deleteContentTag(content);
		
		if(!content.matches(Regexp.CHAL_CONTENT)) throw new Exception("없는데 뭔 추천을 해?");

		//쨈미니 임베딩
		float[] textEmbedding = geminiService.getEmbedding(title+" "+content);

		//임베딩된 벡터 이리와
		List<ChallengeCategory> categoryVectors = dao.selectEmbeddings(sqlSession);
		
		//랜덤 투영
		textEmbedding = VectorUtil.randomProject(textEmbedding);

		//각 카테고리와의 코사인 유사도 계산
		List<CategorySimilarity> similarities = new ArrayList<>();
		for (ChallengeCategory category : categoryVectors) {
			String embeddingJson = category.getEmbedding();

			//JSON -> float[]
			float[] categoryVector = VectorUtil.parseJsonToFloatArray(embeddingJson);

			//계산 중...
			double similarity = VectorUtil.calculateCosineSimilarity(textEmbedding, categoryVector);

			similarities.add(new CategorySimilarity(category.getCategoryNo(), similarity));
		}

		//유사도 기준 내림차순 정렬
		similarities.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

		//상위 3개 카테고리 번호 추출
		List<Integer> topCategories = similarities.stream().limit(3).map(CategorySimilarity::getCategoryNo)
				.collect(Collectors.toList());

		//결과 반환
		return ResponseEntity.ok(topCategories);
	}

}
