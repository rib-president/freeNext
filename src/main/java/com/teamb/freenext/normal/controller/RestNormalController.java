package com.teamb.freenext.normal.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.freenext.member.service.MemberService;
import com.teamb.freenext.normal.service.NormalService;
import com.teamb.freenext.vo.MemberVo;
import com.teamb.freenext.vo.MyScrapVo;

@RestController
@RequestMapping("/normal/*")
public class RestNormalController {

	@Autowired
	private NormalService normalService;
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("getMainData")
	public HashMap<String, Object> getMainData(HttpSession session) {
		
		HashMap<String, Object> data = new HashMap<>();
		
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");
		data.put("mainData", normalService.getMainData(sessionUser));
		data.put("hotProjectList", normalService.getHotProjectShuffle(6, sessionUser));
		
		return data;
	}
	
	@RequestMapping("getProjectList")
	public HashMap<String, Object> getProjectList(@RequestBody HashMap<String, Object> searchData,           
												HttpSession session) {	

		HashMap<String, Object> data = new HashMap<>();		

		if(((ArrayList<Integer>) searchData.get("job_no_list")).size() == 0) {
			searchData.put("job_no_list", null);
		}

		if(((ArrayList<Integer>) searchData.get("local_no_list")).size() == 0) {
			searchData.put("local_no_list", null);
		}
		
		ArrayList<Integer> recruit_list = ((ArrayList<Integer>) searchData.get("recruit_list"));
		if(recruit_list.size() == 1) {

			searchData.put("recruitType_no", Integer.parseInt(String.valueOf(recruit_list.get(0))) + 2 );
		} else {
			searchData.put("recruitType_no", 0);
		}

		if(String.valueOf(searchData.get("searchKeyword")).equals("null")) {
			searchData.put("searchKeyword", null);
		};
		
		int pageNum = Integer.valueOf(String.valueOf(searchData.get("pageNum")));
		int startNum = (pageNum-1) * 7;
		
		searchData.put("startNum", startNum);
		
		/*for(String key : searchData.keySet()) {
			System.out.println("key : " + key + ", value : " + searchData.get(key));
		}*/
		
		int totalCount = normalService.getProjectBoardCount(searchData);
						
		
		int totalPage = (int)Math.ceil(totalCount/7.0);
		
		int startPage = ((pageNum-1)/10)*10 + 1;
		int endPage = ((pageNum-1)/10 + 1)*(10);
		
		if(totalCount == 0) {
			endPage = startPage;
		} else if(endPage > totalPage) {
			endPage = totalPage;
		}		
		
		//System.out.println("totalCount : " + totalCount);
		
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");
		
		searchData.put("sessionUser", sessionUser);
		
		data.put("result", normalService.getProjectList(searchData));
		data.put("hotResult", normalService.getHotProjectShuffle(3, sessionUser));
		data.put("startPage", startPage);
		data.put("endPage", endPage);
		data.put("currentPage", pageNum);
		data.put("totalPage", totalPage);
		
		return data;
	}
	
	@RequestMapping("getProjectDetail")
	public HashMap<String, Object> getProjectDetail(int project_no, HttpSession session) {
		HashMap<String, Object> data = new HashMap<>();
		
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");
		if(sessionUser != null) {
			MyScrapVo scrapVo = new MyScrapVo();
			
			scrapVo.setMember_no(sessionUser.getMember_no());
			scrapVo.setProject_no(project_no);
			
			data.put("myScrap", normalService.getMyScrapCount(scrapVo));
		}
		
		data.put("data", normalService.getProjectDetailPage(project_no, true));
		data.put("scrapCount", normalService.getTotalScrapCount(project_no));		
		
		return data;
	}
	
	@RequestMapping("getRelativeProject")
	public HashMap<String, Object> getRelativeProject(int[] job_no_list, HttpSession session) {
		HashMap<String, Object> data = new HashMap<>();
		
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");
		
		data.put("result", normalService.getRelativeProjectList(job_no_list, sessionUser));
		
		return data;
	}
	
	@RequestMapping("doScrap")
	public HashMap<String,Object>doScrap(MyScrapVo vo, HttpSession session){
						
		HashMap<String,Object> data = new HashMap<String,Object>();
		
		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
		if(sessionUser == null) {
			  data.put("result","error");
			  data.put("reason", "일반회원 로그인이 필요합니다");
			  return data;
		}
		
		int memberNo= sessionUser.getMember_no();
		vo.setMember_no(memberNo);
		
		normalService.doScrap(vo);
		data.put("result","success");
		
		return data;
	}
	
	@RequestMapping("getMyScrapState")
	public HashMap<String,Object>getMyScrapState(MyScrapVo vo, HttpSession session){
		HashMap<String,Object> data = new HashMap<String,Object>();
		
		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
		if(sessionUser == null) {
			  data.put("result","error");
			  data.put("reason", "로그인이 필요합니다");
			  return data;
		}
		
		vo.setMember_no(sessionUser.getMember_no());
		
		int myScrapCount = normalService.getMyScrapCount(vo);
		
		data.put("result", "success");
		if(myScrapCount>0) {
			data.put("state","scrap");
		}else {
			data.put("state","unscrap");
		}
		return data;
	
	}
	
	@RequestMapping("getTotalScrapState")
	public HashMap<String,Object>getTotalScrapState(int project_no){
		HashMap<String,Object> data = new HashMap<String,Object>();
		
		int totalScrapCount = normalService.getTotalScrapCount(project_no);
		data.put("totalScrapCount",totalScrapCount);
		
		return data;	
	
	}
	
	@RequestMapping("getUnReadAlarmCount")
	public HashMap<String, Object> getUnReadAlarmCount(int member_no) {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("unReadCount", normalService.getUnReadAlarmCount(member_no));
		
		return data;
	}
	
	@RequestMapping("getDesired")
	public HashMap<String, Object> getDesired(int member_no) {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("desired", normalService.getDesired(member_no));
		
		return data;
	}
	
	@RequestMapping("getAlarmList")
	public HashMap<String, Object> getAlarmList(String category, int tag_no, int member_no, int pageNum, HttpSession session) {
		HashMap<String, Object> data = new HashMap<>();
		
		HashMap<String, Object> searchData = new HashMap<>();
		searchData.put("category", category);
		searchData.put("tag_no", tag_no);
		searchData.put("member_no", member_no);
		searchData.put("startNum", (pageNum-1)*12);		
		
		data.put("adData", normalService.getHotProjectShuffle(3, (MemberVo) session.getAttribute("sessionUser")));
		data.put("alarmData", normalService.getAlarmData(searchData));
		data.put("totalPage", (int)Math.ceil(normalService.getAlarmDataCount(searchData)/12.0));
		
		return data;
	}
	
	@RequestMapping("readAlarm")
	public HashMap<String, Object> readAllAlarm(int member_no, int project_no, HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> data = new HashMap<>();
		
		int cookieFlag = 0;
		String cookieValue = null;
		String str_project_no = "/" + String.valueOf(project_no) + "/";
		
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie : cookies) {
			String cookieName = cookie.getName();

			if(cookieName.equals("readProjectNo")) {
				cookieFlag++;
				cookieValue = cookie.getValue();
				if(cookieValue.indexOf(str_project_no) != -1) {
					int start = cookieValue.indexOf(str_project_no);
					int start2 =  cookieValue.indexOf(str_project_no) + str_project_no.length();
					cookieValue = cookieValue.substring(0, start) + cookieValue.substring(start2, cookieValue.length());
				} else {
					normalService.increaseReadCount(project_no);
				}
				Cookie newCookie = new Cookie("readProjectNo", cookieValue + str_project_no);
				response.addCookie(newCookie);					
				break;
			}
		}
		
		if(cookieFlag == 0) {
			Cookie newCookie = new Cookie("readProjectNo",str_project_no);
			response.addCookie(newCookie);
			normalService.increaseReadCount(project_no);
		}		
		
		
		normalService.readAlarm(member_no, project_no);
		
		data.put("result", "success");
		
		return data;
	}
	
	@RequestMapping("getScrapProjectList")
	public HashMap<String, Object> getScrapProjectList(int member_no, int pageNum) {
		HashMap<String, Object> data = new HashMap<>();		
		
		data.put("scrapList", normalService.getScrapProjectList(member_no, (pageNum-1)*10));
		data.put("totalPage", (int)Math.ceil(normalService.getScrapCount(member_no)/10.0));
		
		return data;
	}
	
	@RequestMapping("unScrap")
	public HashMap<String, Object> unScrap(int member_no, int project_no) {
		HashMap<String, Object> data = new HashMap<>();
		
		MyScrapVo vo = new MyScrapVo();		
		vo.setMember_no(member_no);
		vo.setProject_no(project_no);
		
		normalService.unScrap(vo);
		
		data.put("result", "success");
		
		return data;
	}
	
	@RequestMapping("getProfile")
	public HashMap<String, Object> getProfile(int member_no, String member_type) {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("memberInfo", normalService.getMemberInfo(member_no, member_type));
		
		if(member_type.equals("N")) {
			data.put("desiredData", normalService.getDesired(member_no));
			data.put("jobList", memberService.getJobCategoryList());
			data.put("localList", memberService.getLocalCategoryList());			
		}		
		return data;
	}
	
	@RequestMapping("getMyProjectList")
	public HashMap<String, Object> getMyProjectList(int member_no, int pageNum, String searchKeyword, String project_state) {
		HashMap<String, Object> data = new HashMap<>();		
		
		HashMap<String, Object> searchData = new HashMap<>();
		searchData.put("member_no", member_no);
		searchData.put("searchKeyword", searchKeyword);
		searchData.put("project_state", project_state);
		
		int startNum = (pageNum-1) * 5;
		
		int totalCount = normalService.getMyProjectListCount(searchData);
		
		int totalPage = (int)Math.ceil(totalCount/5.0);
		
		int startPage = ((pageNum-1)/5)*5 + 1;
		int endPage = ((pageNum-1)/5 + 1)*(5);
		
		if(totalCount == 0) {
			endPage = startPage;
		} else if(endPage > totalPage) {
			endPage = totalPage;
		}		
		
		if(searchKeyword.equals("null") || searchKeyword.equals("")) {
			searchData.put("searchKeyword", null);
		};		
		
		searchData.put("startNum", startNum);
		
		
		data.put("projectList", normalService.getMyProjectList(searchData));
		data.put("totalCount", totalCount);
		data.put("startPage", startPage);
		data.put("endPage", endPage);
		data.put("currentPage", pageNum);
		data.put("totalPage", totalPage);
		
		return data;
	}
	
	@RequestMapping("modifyProjectState")
	public HashMap<String, Object> modifyProjectState(int project_no, String project_state) {
		HashMap<String, Object> data = new HashMap<>();
		
		normalService.modifyProjectState(project_no, project_state);
		
		return data;
	}
	
}