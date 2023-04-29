package com.teamb.freenext.scraping.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teamb.freenext.scraping.mapper.ScrapingMapper;

@Component
public class Rename {


	private String kakaoKey;
	private static final int COOPERATION_NO = 3;
	
	
	@Autowired
	private ScrapingMapper scrapingMapper;
	
	
	
	
	public Rename() {
		super();
	}


	public Rename(String kakaoKey) {
		this.kakaoKey = kakaoKey;
	}
	
	
	public void crawl() {

		if(isExistKakaoKey()) {
			String boardNumber = getStartCrawlingBoardNumber();
			
			
			
		}
	}

	
	private boolean isExistKakaoKey() {
		return kakaoKey != null;
	}
	
	
	private String getStartCrawlingBoardNumber() {
		return scrapingMapper.selectStartKey(COOPERATION_NO);
	}
	
}
