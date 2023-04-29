package com.teamb.freenext.scraping.module;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.teamb.freenext.scraping.mapper.ScrapingMapper;

@Component
public class DesignGroupNineCrawler implements Crawler {

	@Autowired
	private ScrapingMapper scrapingMapper;
	
	private String kakaoKey;
	private static final int COOPERATION_NO = 3;

	
	
	public DesignGroupNineCrawler() {
		super();
	}


	public  DesignGroupNineCrawler(String kakaoKey) {
		this.kakaoKey = kakaoKey;
	}
	

	/*
	public static DesignGroupNineCrawler asKakaoKey(String kakaoKey) {
		return new DesignGroupNineCrawler(kakaoKey);
	}
	*/
	
	@Override
	public void crawl() {
		
		Document document = request();
		refine(document);
		save();
	}
	
	
	private Document request() {
		
		String board_no = getStartCrawlingBoardNumber();
		
		Connection conn = null;			
		Document document = null;
		String URL = "http://designnine.co.kr/project/project_list1.html?PHPSESSID=7cb3863d8bc2b28eb99a337710148688";
		conn = Jsoup.connect(URL);
		try {
			document = conn.get();
		} catch (IOException e) {
			System.out.println("최신 글 번호 connect 익셉션]" + e.getMessage());
		}

		return document;
	}
	
	private void refine(Document document) {
		
	}
	
	private void save() {
		
	}
	
	
	private String getStartCrawlingBoardNumber() {
		return scrapingMapper.selectStartKey(COOPERATION_NO);
	}


}
