package com.kh.spring.videoCall.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.kh.spring.util.common.Regexp;

@Component
public class CachingParticipants {
	//스뿌륑 캐쉬 매니쟈
    @Autowired
    private CacheManager cacheManager;

    //캐싱하기
    public void update(Map<String, Integer> participantsList) {
        Cache cache = cacheManager.getCache(Regexp.CACHE_NAME);
        if (cache != null) {
            cache.put("allParticipants", participantsList);
        }
    }

    // 캐시된 리스트 꺼내기
    public Map<String, Integer> get() {
        Cache cache = cacheManager.getCache(Regexp.CACHE_NAME);
        if (cache != null) {
            return cache.get("allParticipants", Map.class);
        }
        return null;
    }
}
