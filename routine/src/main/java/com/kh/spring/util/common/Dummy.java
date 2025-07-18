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
		list.add(new ChallengeCategory(100,"더미"));
		list.add(new ChallengeCategory(101,"뭘봐요"));
		list.add(new ChallengeCategory(102,"뭐요"));
		list.add(new ChallengeCategory(110,"안아줘요"));
		list.add(new ChallengeCategory(111,"배고파요"));
		list.add(new ChallengeCategory(112,"집보내줘요"));
		return list;
	}
}
