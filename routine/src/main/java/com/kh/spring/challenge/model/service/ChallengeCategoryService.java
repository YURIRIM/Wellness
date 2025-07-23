package com.kh.spring.challenge.model.service;

import java.util.List;

import com.kh.spring.challenge.model.vo.ChallengeCategory;

public interface ChallengeCategoryService {

	List<ChallengeCategory> selectCCList() throws Exception;

}
