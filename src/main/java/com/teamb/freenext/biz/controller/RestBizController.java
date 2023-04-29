package com.teamb.freenext.biz.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.freenext.biz.service.BizService;
import com.teamb.freenext.commons.KakaoRestAPI;
import com.teamb.freenext.member.service.MemberService;
import com.teamb.freenext.normal.service.NormalService;
import com.teamb.freenext.vo.KakaopayVo;
import com.teamb.freenext.vo.MemberVo;


@RestController
@RequestMapping("/biz/*")
public class RestBizController {

	@Autowired
	private BizService bizService;
	
	@Autowired
	private NormalService normalService;
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("payToKakao")
	public HashMap<String, Object> payToKakao(String item_name, String quantity, int total_amount, HttpSession session) {

		String kakaoKey = bizService.getKakaoKey();

		HashMap<String, Object> data = new HashMap<>();
		
		KakaoRestAPI kakao = new KakaoRestAPI();
		String partner_order_id = UUID.randomUUID().toString();
		String partner_user_id = ((MemberVo) session.getAttribute("sessionBizUser")).getMember_id();
		
		Map<String, Object> result = kakao.payReady(partner_order_id, partner_user_id, item_name, quantity, String.valueOf(total_amount), kakaoKey);				

		KakaopayVo vo = new KakaopayVo();
		
		vo.setTid((String) result.get("tid"));
		vo.setPartner_order_id(partner_order_id);
		vo.setTotal_amount(total_amount);		

		session.setAttribute("kakaopayVo", vo);

		data.put("next_redirect_pc_url", result.get("next_redirect_pc_url"));
		data.put("tid", result.get("tid"));

		return data;
	}
	
//	@RequestMapping("getResponseCode")
//	public HashMap<String, Object> getResponseCode(HttpSession session) {
//
//		HashMap<String, Object> data = new HashMap<>();
//		
//		data.put("response_code", session.getAttribute("response_code"));
//		data.put("tid", session.getAttribute("tid"));
//
//		session.removeAttribute("response_code");
//		session.removeAttribute("tid");
//		
//		return data;
//	}
	
	@RequestMapping("subAd")
	public HashMap<String, Object> subAd(int project_no) {
		
		HashMap<String, Object> data = new HashMap<>();
		
		bizService.subAd(project_no);		
		
		data.put("result", "sucess");
		data.put("ad_endDate", bizService.getProjectAd(project_no).getAd_endDate());
		
		return data;
	}
	
	@RequestMapping("deleteProject")
	public HashMap<String, Object> deleteProject(int project_no) {
		HashMap<String, Object> data = new HashMap<>();
		
		bizService.deleteBoard(project_no);
		
		return data;
	}
	
	@RequestMapping("modifyProjectPage")
	public HashMap<String, Object> modifyProjectPage(int project_no) {
		
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("data", normalService.getProjectDetailPage(project_no, false));
		data.put("jobCategoryList", bizService.getJobCategoryList());
		data.put("workTypeCategoryList", bizService.getWorkTypeCateogoryList());
		data.put("recruitTypeCategoryList", bizService.getRecruitTypeCategoryList());
		data.put("localCategoryList", memberService.getLocalCategoryList());
		data.put("adVo", bizService.getProjectAd(project_no));
		
		return data;
	}
	
	@RequestMapping("modifyProjectProcess")
	public HashMap<String, Object> modifyProjectProcess(@RequestBody HashMap<String, Object> receivedData) {		
		HashMap<String, Object> data = new HashMap<>();
		
		bizService.modifyProjectProcess(receivedData);
		
		data.put("project_no", receivedData.get("project_no"));
		
		return data;
	}
	
	@RequestMapping("getMyProjectStatus")
	public HashMap<String, Object> getMyProjectStatus(int member_no) {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("scrapCountList", bizService.getMyProjectScrapCount(member_no));
		data.put("weekList", bizService.getWeekList());
		data.put("projectList", bizService.getMyProjectListAll(member_no));
		
		return data;
	}
}
