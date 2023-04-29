package com.teamb.freenext.scraping.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teamb.freenext.scraping.mapper.ScrapingMapper;

@Component
public class KakaoKeyManager {

	@Autowired
	private ScrapingMapper scrapingMapper;

	
	
	
	public String getKakaoKey() {
		return scrapingMapper.selectKakaoKey();
	}

	
}
