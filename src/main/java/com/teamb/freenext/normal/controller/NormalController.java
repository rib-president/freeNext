package com.teamb.freenext.normal.controller;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.teamb.freenext.member.service.MemberService;
import com.teamb.freenext.normal.service.NormalService;
import com.teamb.freenext.vo.MemberVo;
import com.teamb.freenext.vo.MyScrapVo;

@Controller
@RequestMapping("/normal/*")
public class NormalController {

	@Autowired
	private NormalService normalService;
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("main")
	public String main() {
		
		return "normal/main";
	}
	
//	@RequestMapping("main")
//	public String main(Model model, HttpSession session) {
//		
//		ArrayList<HashMap<String, Object>> datalist = normalService.getLatestProjectList();
//		
//		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
//		if(sessionUser != null) {
//			//로그인을 했을때...
//			int memberNo = sessionUser.getMember_no();
//			
//			for(HashMap<String, Object> data : datalist) {
//				MyScrapVo myScrapVo = new MyScrapVo();
//				
//				 myScrapVo.setMember_no(memberNo);
//				 myScrapVo.setProject_no(((ProjectBoardVo) data.get("projectBoardVo")).getProject_no());
//				 
//				 int myScrapCount = normalService.getMyScrapCount(myScrapVo);
//
//				 data.put("myScrapCount", myScrapCount);
//			}		
//		} else {
//			for(HashMap<String, Object> data : datalist) {
//				data.put("myScrapCount", 0);
//			}
//		}
//		
//		model.addAttribute("resultList", datalist);
//		
//		return "normal/main";
//	}

	@RequestMapping("projectListPage")
	public String projectListPage(String searchKeyword,								
								@RequestParam(value="recruitType_no", defaultValue="0") int recruitType_no,
								int[] job_no_list, Model model) {								
	
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("recruitType_no", recruitType_no);
		if(job_no_list != null) {
			model.addAttribute("job_no_list", job_no_list[0]);	
		}		
		model.addAttribute("localCategoryList", memberService.getLocalCategoryList());

		
		return "normal/projectListPage";
	}
	
	@RequestMapping("projectDetailPage")
	public String projectDetailPage(int project_no, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");
		if(sessionUser != null) {
			MyScrapVo scrapVo = new MyScrapVo();
			
			scrapVo.setMember_no(sessionUser.getMember_no());
			scrapVo.setProject_no(project_no);
			
			model.addAttribute("myScrap", normalService.getMyScrapCount(scrapVo));
		}
		
		
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
		
		
		model.addAttribute("data", normalService.getProjectDetailPage(project_no, true));
		model.addAttribute("scrapCount", normalService.getTotalScrapCount(project_no));
		
		if(cookieValue != null) {
			String[] str_list = cookieValue.split("/");
			ArrayList<Integer> project_no_list = new ArrayList<>();
			for(String str : str_list) {
				if(!str.equals("")) {
					project_no_list.add(Integer.parseInt(str));	
				}								
			}
			Collections.reverse(project_no_list);
			model.addAttribute("seenProjectList", normalService.getSeenProjectList(project_no_list));			
		}
		
		return "normal/projectDetailPage";
	}
	
	@RequestMapping("myBoxPage")
	public String myBoxPage(Model model, HttpSession session) {
		
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");		
		if(sessionUser != null) {
			return "normal/myBoxPage";			
		}
		
		MemberVo sessionBizUser = (MemberVo) session.getAttribute("sessionBizUser");		
		if(sessionBizUser != null) {
			return "normal/bizBoxPage";			
		}		
		
		return "normal/main";
	}
	
	/*@RequestMapping("projectListPage")
	public String projectListPage(
			Model model, HttpSession session,
			@RequestParam(value="recruitType_no", defaultValue = "0") Integer recruitType_no ,
			@RequestParam(value = "pageNum" , defaultValue = "1") int pageNum ,
			@RequestParam(value = "project_state" , defaultValue = "0") String project_state,
			@RequestParam(value = "job_no" , defaultValue = "0") int job_no) {
		
		ArrayList<HashMap<String, Object>> datalist = normalService.getProjectBoardList(recruitType_no,pageNum,project_state,job_no);
		ArrayList<HashMap<String, Object>> hotlist = normalService.getHotBoardList(recruitType_no, project_state, job_no);
		ArrayList<HashMap<String, Object>> mainlist = normalService.getMainBoardList(recruitType_no, project_state, job_no);
		
		int count = normalService.getProjectBoardCount(recruitType_no,project_state,job_no);
		int totalPageCount = (int)Math.ceil(count/10.0);
		
		int startPage = ((pageNum-1)/10)*10 + 1;
		int endPage = ((pageNum-1)/10 + 1)*(10);
		
		if(totalPageCount == 0) {
			endPage = startPage;
		}else if(endPage > totalPageCount) {
			endPage = totalPageCount;
		}

		String additionalParam = "";
		
		if(recruitType_no != null) {
			additionalParam += "recruitType_no=" + recruitType_no;
		}
		
		String projectStateParam = "";
		
		if(projectStateParam != null) {
			projectStateParam += "project_state=" + project_state; 
		}
		
		String jobParam="";
		
		if(jobParam != null) {
			jobParam += "job_no=" + job_no;
		}
		
		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
		if(sessionUser != null) {
			//로그인을 했을때...
			int memberNo = sessionUser.getMember_no();
			
			for(HashMap<String, Object> data : datalist) {
				MyScrapVo myScrapVo = new MyScrapVo();
				
				 myScrapVo.setMember_no(memberNo);
				 myScrapVo.setProject_no(((ProjectBoardVo) data.get("boardVo")).getProject_no());
				 
				 int myScrapCount = normalService.getMyScrapCount(myScrapVo);

				 data.put("myScrapCount", myScrapCount);
			}		
		} else {
			for(HashMap<String, Object> data : datalist) {
				data.put("myScrapCount", 0);
			}
		}		
		
		model.addAttribute("jobParam", jobParam);
		model.addAttribute("projectStateParam", projectStateParam);
		model.addAttribute("additionalParam", additionalParam);
		model.addAttribute("count", count);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("hotlist", hotlist);
		model.addAttribute("mainlist", mainlist);
		model.addAttribute("datalist", datalist);
		
		model.addAttribute("job_no", job_no); // 직종 : ex) 디자인,프로그래머,웹 등
		model.addAttribute("projectState", project_state); // 진행중,마감
		model.addAttribute("rt_no", recruitType_no); // 프리랜서 매칭, IT 아웃소싱
		return "normal/projectListPage";
	}*/
	
//	@RequestMapping("matchingDetailPage")
//	public String matchingDetailPage( int project_no, Model model, HttpSession session) {
//		
//		HashMap<String, Object> map = normalService.getProjectDetailPage(project_no, true);
//		model.addAttribute("data", map);				
//		
//		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
//		
//		int totalScrapCount = normalService.getTotalScrapCount(project_no);
//		model.addAttribute("totalScrapCount",totalScrapCount);		
//		if(sessionUser != null) {
//			//로그인을 했을때...
//			int memberNo = sessionUser.getMember_no();
//			MyScrapVo myScrapVo = new MyScrapVo();
//			myScrapVo.setMember_no(memberNo);
//			myScrapVo.setProject_no(project_no);
//			
//			int myScrapCount = normalService.getMyScrapCount(myScrapVo);
//			model.addAttribute("myScrapCount",myScrapCount);
//			model.addAttribute("myMemberNo",memberNo);		
//		}
//	
//		return "normal/matchingDetailPage";
//	}
//	
//	@RequestMapping("outsourcingDetailPage")
//	public String outsourcingDetailPage( int project_no,Model model,HttpSession session) {
//		
//		HashMap<String, Object> map = normalService.getProjectDetailPage(project_no, true);
//		model.addAttribute("data", map);
//		
//		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
//		
//		int totalScrapCount = normalService.getTotalScrapCount(project_no);
//		model.addAttribute("totalScrapCount",totalScrapCount);
//		if(sessionUser != null) {
//			//로그인을 했을때...
//			int memberNo = sessionUser.getMember_no();
//			MyScrapVo myScrapVo = new MyScrapVo();
//			myScrapVo.setMember_no(memberNo);
//			myScrapVo.setProject_no(project_no);
//			
//			int myScrapCount = normalService.getMyScrapCount(myScrapVo);
//			model.addAttribute("myScrapCount",myScrapCount);
//			model.addAttribute("myMemberNo",memberNo);		
//		}
//				
//		return "normal/outsourcingDetailPage";
//	
//	}	

//	@RequestMapping("scrapProcess")
//	public String scrapProcess(MyScrapVo svo, HttpSession session,ProjectBoardVo pvo) {
//		
//		//행위자 정보는 세션에서 꼭 뽑아오자...
//		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
//		int memberNo = sessionUser.getMember_no();
//		svo.setMember_no(memberNo);
//		
//		
//		
//		int projectNo = pvo.getProject_no();
//		pvo.setProject_no(projectNo);
//		int recruitNo = pvo.getRecruitType_no();
//		
//		normalService.doScrap(svo);
//		
//		if(recruitNo ==1) {
//			return "redirect:./matchingDetailPage?project_no="+pvo.getProject_no();
//		}
//			
//		return "redirect:./outsourcingDetailPage?project_no="+pvo.getProject_no();
//	}
	
	@RequestMapping("deleteMyScrapProcess")
	public String deleteMyScrapProcess(int[] scrap_no_list ) {
		
		for(int scrap_no : scrap_no_list) {
			normalService.deleteMyScrapByList(scrap_no);
		}
		
		return "redirect:./myBoxPage";
	}	
	
//	@RequestMapping("myBoxPage")
//	public String myScrapPage(Model model,HttpSession session, @RequestParam(value="orderBy", defaultValue = "scrap") String orderBy) {
//		MemberVo sessionUser = (MemberVo)session.getAttribute("sessionUser");
//		int memberNo = sessionUser.getMember_no();
//
//		ArrayList<HashMap<String,Object>> scrapList = normalService.getMyScrapList(memberNo, orderBy);
//		
//		if(sessionUser!=null) {
//			MyScrapVo myScrapVo = new MyScrapVo();
//			myScrapVo.setMember_no(memberNo);
//			
//			model.addAttribute("scrapList",scrapList);
//			model.addAttribute("memberNo",memberNo);
//		}
//		return "normal/myBoxPage";		
//	}
}
