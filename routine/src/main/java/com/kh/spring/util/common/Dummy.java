package com.kh.spring.util.common;

import java.util.ArrayList;
import java.util.List;

import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.user.model.vo.User;

public class Dummy {
	public static User dummyUser() {
		User user = new User();
		user.setUserNo(0);
		user.setName("Name");
		user.setNick("Dummy");
		user.setEmail("dummy@data.net");
		return user;
	}
	
	public static List<ChallengeCategory> dummyCC(){
		List<ChallengeCategory> list = new ArrayList<>();
		list.add(ChallengeCategory.builder().categoryNo(100).categoryName("애애앵").build());
		list.add(ChallengeCategory.builder().categoryNo(101).categoryName("뭘봐요").build());
		list.add(ChallengeCategory.builder().categoryNo(102).categoryName("저는기어다닐거에요").build());
		list.add(ChallengeCategory.builder().categoryNo(110).categoryName("안아줘요").build());
		list.add(ChallengeCategory.builder().categoryNo(111).categoryName("뱃속에고기가있어요").build());
		list.add(ChallengeCategory.builder().categoryNo(112).categoryName("집보내줘요").build());
		return list;
	}
}
