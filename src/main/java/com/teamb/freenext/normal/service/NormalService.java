package com.teamb.freenext.normal.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamb.freenext.commons.StringUtil;
import com.teamb.freenext.normal.mapper.NormalMapper;
import com.teamb.freenext.vo.CooperationCategoryVo;
import com.teamb.freenext.vo.JobCategoryVo;
import com.teamb.freenext.vo.LocalCategoryVo;
import com.teamb.freenext.vo.MemberVo;
import com.teamb.freenext.vo.MyScrapVo;
import com.teamb.freenext.vo.ProjectBoardVo;
import com.teamb.freenext.vo.RecruitTypeCategoryVo;
import com.teamb.freenext.vo.WorkTypeCategoryVo;
import com.zaxxer.hikari.util.SuspendResumeLock;

@Service
public class NormalService {
	
	@Autowired
	private NormalMapper normalMapper;

	
	public HashMap<String, Object> getMainData(MemberVo sessionUser) {
		HashMap<String, Object> dataMap = new HashMap<>();
		
		ArrayList<HashMap<String, Object>> newProjectList = new ArrayList<>();
		for(ProjectBoardVo vo : normalMapper.selectNewProject()) {
			HashMap<String, Object> map = new HashMap<>();
			int project_no = vo.getProject_no();
			vo.setProject_content(StringUtil.escapeHTML(vo.getProject_content()).replaceAll("<br>|&nbsp;", " "));
			
			map.put("projectVo", vo);
			map.put("localVo", normalMapper.getLocalByNo(project_no));
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("workTypeVo", normalMapper.getWorkTypeByNo(vo.getWorkType_no()));
			map.put("recruitTypeVo", normalMapper.getRecruitTypeByNo(vo.getRecruitType_no()));
			map.put("cooperationVo", normalMapper.getCooperationByNo(vo.getCooperation_no()));
			map.put("scrapCount", normalMapper.getTotalScrapCount(project_no));
			
			if(sessionUser != null) {
				MyScrapVo myScrapVo = new MyScrapVo();
				myScrapVo.setMember_no(sessionUser.getMember_no());
				myScrapVo.setProject_no(project_no);
				
				map.put("myScrap", normalMapper.getMyScrapCount(myScrapVo));
			}
			newProjectList.add(map);
		}
		
		dataMap.put("totalProject", normalMapper.getTotalProjectCount());
		dataMap.put("totalCustomer", normalMapper.getTotalCustomerCount());
		dataMap.put("totalCompany", normalMapper.getTotalCompanyCount());
		dataMap.put("newProjectList", newProjectList);
		
		return dataMap;
		
	}
	
	public ArrayList<HashMap<String, Object>> getHotProjectShuffle(int projectNum, MemberVo sessionUser) {
		
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		for(ProjectBoardVo vo : normalMapper.selectHotProjectShuffle(projectNum)) {
			HashMap<String, Object> map = new HashMap<>();
			
			int project_no = vo.getProject_no();
			vo.setProject_content(StringUtil.escapeHTML(vo.getProject_content()).replaceAll("<br>|&nbsp;", " "));
			map.put("projectVo", vo);
			map.put("memberVo", normalMapper.getMemberByNo(vo.getMember_no()));
			map.put("workTypeVo", normalMapper.getWorkTypeByNo(vo.getWorkType_no()));
			map.put("localVo", normalMapper.getLocalByNo(project_no));
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("cooperationVo", normalMapper.getCooperationByNo(vo.getCooperation_no()));
			map.put("scrapCount", normalMapper.getTotalScrapCount(project_no));
			map.put("recruitTypeVo", normalMapper.getRecruitTypeByNo(vo.getRecruitType_no()));
			
			if(sessionUser != null) {
				MyScrapVo scrapVo = new MyScrapVo();
				scrapVo.setMember_no(sessionUser.getMember_no());
				scrapVo.setProject_no(project_no);
				
				map.put("myScrap", normalMapper.getMyScrapCount(scrapVo));
			} else {
				map.put("myScrap", 0);
			}			
			
			resultList.add(map);
		}
		
		return resultList;
	}
	
	public ArrayList<HashMap<String, Object>> getProjectList(HashMap<String, Object> searchData) {
		
		ArrayList<HashMap<String, Object>> mapList= normalMapper.selectProjectList(searchData);			
		
		for(HashMap<String, Object> map : mapList) {
			int project_no = ((Integer) map.get("project_no"));
			map.put("project_content", StringUtil.escapeHTML(((String) map.get("project_content"))).replaceAll("<br>|&nbsp;", " "));
			map.put("workTypeVo", normalMapper.getWorkTypeByNo(((Integer) map.get("workType_no"))));	
			map.put("localVo", normalMapper.getLocalByNo(project_no));
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("cooperationVo", normalMapper.getCooperationByNo((Integer) map.get("cooperation_no")));
			map.put("scrapCount", normalMapper.getTotalScrapCount(project_no));
			map.put("recruitTypeVo", normalMapper.getRecruitTypeByNo((Integer) map.get("recruitType_no")));
			
			MemberVo sessionUser = (MemberVo) searchData.get("sessionUser");
			if(sessionUser != null) {
				MyScrapVo scrapVo = new MyScrapVo();
				scrapVo.setMember_no(sessionUser.getMember_no());
				scrapVo.setProject_no(project_no);
				
				map.put("myScrap", normalMapper.getMyScrapCount(scrapVo));
			}
		}
		
		return mapList;
	}
	
	public int getProjectBoardCount(HashMap<String, Object> searchData) {
		return normalMapper.selectProjectListCount(searchData);
	}
	
//	public ArrayList<HashMap<String, Object>> getLatestProjectList() {
//		
//		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>(); 
//		
//		for(ProjectBoardVo projectBoardVo : normalMapper.selectLatestProjectList()) {
//			HashMap<String, Object> map = new HashMap<>();
//			
//			map.put("projectBoardVo", projectBoardVo);
//
//			map.put("jobCategoryVo", normalMapper.selectJobCategoryByProjectNo(projectBoardVo.getProject_no()));
//			map.put("cooperationCategoryVo", normalMapper.selectCooperationCategoryByProjectNo(projectBoardVo.getProject_no()));			
//			
//			resultList.add(map);			
//		}
//		
//		return resultList;
//	}
	
	public HashMap<String,Object> getProjectDetailPage(int project_no, boolean isEscape) {
		
		HashMap<String , Object> map = new HashMap<String , Object> ();
		//프리랜서 매칭, IT아웃소싱  공통 상세 페이지 페이지 가져오기
		ProjectBoardVo projectBoardVo = normalMapper.getProjectDetailPageByNo(project_no);
		if(isEscape) {
			projectBoardVo.setProject_content(StringUtil.escapeHTML(projectBoardVo.getProject_content()));
		}
		// 직종 가져오기
		ArrayList<JobCategoryVo> jobCategoryVoList  = normalMapper.selectJobCategoryByProjectNo(project_no);	
		// 지역 가져오기
		LocalCategoryVo localCategoryVo = normalMapper.getLocalByNo(project_no);
		
		//기업명 가져오기
		int memberNo = projectBoardVo.getMember_no();
		MemberVo memberVo = normalMapper.getMemberByNo(memberNo);
		
		int recruitTypeNo = projectBoardVo.getRecruitType_no();
		RecruitTypeCategoryVo recruitTypeCategoryVo = normalMapper.getRecruitTypeByNo(recruitTypeNo);
		
		int workTypeNo = projectBoardVo.getWorkType_no();
		WorkTypeCategoryVo workTypeCategoryVo = normalMapper.getWorkTypeByNo(workTypeNo);
		
		int cooperationNo = projectBoardVo.getCooperation_no();
		CooperationCategoryVo cooperationCategoryVo = normalMapper.getCooperationByNo(cooperationNo);
		
		map.put("memberVo", memberVo);
		map.put("projectBoardVo",projectBoardVo);
		map.put("jobCategoryVoList", jobCategoryVoList);
		map.put("localCategoryVo",localCategoryVo);	
		map.put("recruitTypeCategoryVo",recruitTypeCategoryVo);
		map.put("workTypeCategoryVo", workTypeCategoryVo);
		map.put("cooperationCategoryVo",cooperationCategoryVo);
		return map;
	}	
	
	public void increaseReadCount(int project_no) {
		normalMapper.updateProjectReadCount(project_no);
	}
	
	public ArrayList<HashMap<String, Object>> getRelativeProjectList(int[] job_no_list, MemberVo sessionUser) {
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();
		
		ArrayList<ProjectBoardVo> projectVoList = null;

		projectVoList = normalMapper.selectRelativeProjectShuffle(job_no_list);
		
		for(ProjectBoardVo vo: projectVoList) {
			HashMap<String, Object> map = new HashMap<>();
			
			int project_no = vo.getProject_no();
			
			map.put("memberVo", normalMapper.getMemberByNo(vo.getMember_no()));
			map.put("projectVo", vo);
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("localVo", normalMapper.getLocalByNo(project_no));
			map.put("workTypeVo", normalMapper.getWorkTypeByNo(vo.getWorkType_no()));
			map.put("cooperationVo", normalMapper.getCooperationByNo(vo.getCooperation_no()));
			
			if(sessionUser != null) {
				MyScrapVo myScrapVo = new MyScrapVo();
				
				myScrapVo.setMember_no(sessionUser.getMember_no());
				myScrapVo.setProject_no(project_no);
				
				map.put("myScrap", normalMapper.getMyScrapCount(myScrapVo));
			}			
			dataList.add(map);
		}						
		
		return dataList;
	}
	
	public ArrayList<ProjectBoardVo> getSeenProjectList(ArrayList<Integer> project_no_list) {
		
		ArrayList<ProjectBoardVo> seenProjectList = new ArrayList<>();
		for(int project_no : project_no_list) {
			ProjectBoardVo seenProject = normalMapper.selectSeenProject(project_no);
			if(seenProject != null) {
				seenProjectList.add(seenProject);	
			}			
		}

		return seenProjectList;
	}
	
	public int getUnReadAlarmCount(int member_no) {
		return normalMapper.selectUnReadAlarmCount(member_no);
	}
	
	public HashMap<String, Object> getDesired(int member_no) {
		HashMap<String, Object> desiredMap = new HashMap<>();
		
		desiredMap.put("jobVo", normalMapper.selectDesiredJob(member_no));
		desiredMap.put("localVo", normalMapper.selectDesiredLocal(member_no));
		return desiredMap;
	}
	
	public ArrayList<HashMap<String, Object>> getAlarmData(HashMap<String, Object> searchData) {
		
		ArrayList<HashMap<String, Object>> alarmList = new ArrayList<>();
		for(HashMap<String, Object> map : normalMapper.selectAlarmList(searchData)) {
			int project_no = (Integer) map.get("project_no");
			
			//map.put("memberVo", normalMapper.getMemberByNo((Integer) map.get("member_no")));
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("localVo", normalMapper.getLocalByNo(project_no));
			map.put("workTypeVo", normalMapper.getWorkTypeByNo((Integer) map.get("workType_no")));
			//map.put("cooperationVo", normalMapper.getCooperationByNo((Integer) map.get("cooperation_no"))); 
			
			alarmList.add(map);
		}
		
		return alarmList;
	}
	
	public int getAlarmDataCount(HashMap<String, Object> searchData) {
		
		return normalMapper.selectAlarmListCount(searchData);
	}	
	
	public void readAlarm(int member_no, int project_no) {
		normalMapper.updateReadAlarm(member_no, project_no);
	}
	
	public ArrayList<HashMap<String,Object>> getScrapProjectList(int member_no, int startNum){
		ArrayList<HashMap<String, Object>> scrapList = new ArrayList<HashMap<String, Object>>();
		
		for(ProjectBoardVo projectVo: normalMapper.getScrapProjectList(member_no, startNum)) {
			int project_no = projectVo.getProject_no();						
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("projectVo",projectVo);
			map.put("memberVo", normalMapper.getMemberByNo(projectVo.getMember_no()));
			map.put("cooperationVo", normalMapper.getCooperationByNo(projectVo.getCooperation_no()));			
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("localVo", normalMapper.getLocalByNo(project_no));	
			map.put("recruitTypeVo",normalMapper.getRecruitTypeByNo(projectVo.getRecruitType_no()));
			map.put("workTypeVo", normalMapper.getWorkTypeByNo(projectVo.getWorkType_no()));
			
			scrapList.add(map);
		}		
		return scrapList;	
	}	
	
	public int getScrapCount(int member_no) {
		return normalMapper.getScrapCount(member_no);
	}
	
	public void unScrap(MyScrapVo vo) {
		normalMapper.deleteScrap(vo);
	}
	
	public HashMap<String, Object> getMemberInfo(int member_no, String member_type) {
		return normalMapper.selectMemberInfo(member_no, member_type);
	}
	
	public ArrayList<HashMap<String, Object>> getMyProjectList(HashMap<String, Object> searchData) {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		
		for(ProjectBoardVo projectVo : normalMapper.selectMyProjectList(searchData)) {
			HashMap<String, Object> map = new HashMap<>();
			int project_no = projectVo.getProject_no();

			map.put("projectVo", projectVo);
			map.put("jobVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("localVo", normalMapper.getLocalByNo(project_no));
			map.put("scrapCount", normalMapper.getTotalScrapCount(project_no));
			map.put("isAd", ((normalMapper.isAdProject(project_no)) > 0 ? true : false));
			
			resultList.add(map);
		}
		
		return resultList;
	}
	
	public int getMyProjectListCount(HashMap<String, Object> searchData) {
		return normalMapper.selectMyProjectListCount(searchData);
	}
	
	public void modifyProjectState(int project_no, String project_state) {
		
		normalMapper.updateProjectState(project_no, project_state);
	}
	
	/*===================================================================================================*/
	
	public void doScrap(MyScrapVo vo) {
		int count = normalMapper.getMyScrapCount(vo);
		
		if(count>0) {
			normalMapper.deleteScrap(vo);
		}else {
			normalMapper.doScrap(vo);
		}	
	}
	
	public void deleteMyScrapByList(int scrap_no) {
		normalMapper.deleteMyScrapByList(scrap_no);
	}	
	
	public int getTotalScrapCount(int project_no) {
		return normalMapper.getTotalScrapCount(project_no);
	}
	public int getMyScrapCount(MyScrapVo vo) {
		return normalMapper.getMyScrapCount(vo);
	}	
	
//	public int getProjectBoardCount(int recruitType_no,String project_state,int job_no) {
//		
//		return normalMapper.getProjectBoardCount(recruitType_no,project_state,job_no);
//	}	
	
//	public ArrayList<HashMap<String, Object>> getProjectBoardList(int recruitType_no, int pageNum,String project_state,int job_no) {
//		
//		ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
//		
//		ArrayList<ProjectBoardVo> boardVoList = normalMapper.getProjectBoardList(recruitType_no, pageNum, project_state,job_no);
//		
//		
//		for(ProjectBoardVo boardVo : boardVoList) {
//			
//			JobCategoryVo jobCategoryVo  = normalMapper.selectJobCategoryByProjectNo(boardVo.getProject_no());
//			
//			int rtCategoryNo = boardVo.getRecruitType_no();
//			RecruitTypeCategoryVo rtCategoryVo = normalMapper.getRecruitTypeByNo(rtCategoryNo);
//			
//			int cooCategoryNo = boardVo.getCooperation_no();
//			CooperationCategoryVo cooCategoryVo = normalMapper.getCooperationByNo(cooCategoryNo);
//			
//			int workCategoryNo = boardVo.getWorkType_no();
//			WorkTypeCategoryVo workCategoryVo = normalMapper.getWorkTypeByNo(workCategoryNo);
//			
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			
//			map.put("jobCategoryVo", jobCategoryVo);
//			map.put("rtCategoryVo", rtCategoryVo);
//			map.put("cooCategoryVo", cooCategoryVo);
//			map.put("workCategoryVo", workCategoryVo);
//			
//			map.put("boardVo", boardVo);
//			
//			datalist.add(map);
//		}
//		
//		return datalist;
//	}
	
//	public ArrayList<HashMap<String, Object>> getHotBoardList(int recruitType_no,String project_state,int job_no) {
//		
//		ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
//		
//		ArrayList<ProjectBoardVo> boardVoList = normalMapper.getHotBoardList(recruitType_no, project_state, job_no);
//		
//		for(ProjectBoardVo boardVo : boardVoList) {
//			
//			JobCategoryVo jobCategoryVo  = normalMapper.selectJobCategoryByProjectNo(boardVo.getProject_no());
//			
//			int rtCategoryNo = boardVo.getRecruitType_no();
//			RecruitTypeCategoryVo rtCategoryVo = normalMapper.getRecruitTypeByNo(rtCategoryNo);
//			
//			int cooCategoryNo = boardVo.getCooperation_no();
//			CooperationCategoryVo cooCategoryVo = normalMapper.getCooperationByNo(cooCategoryNo);
//			
//			int workCategoryNo = boardVo.getWorkType_no();
//			WorkTypeCategoryVo workCategoryVo = normalMapper.getWorkTypeByNo(workCategoryNo);
//			
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			
//			map.put("jobCategoryVo", jobCategoryVo);
//			map.put("rtCategoryVo", rtCategoryVo);
//			map.put("cooCategoryVo", cooCategoryVo);
//			map.put("workCategoryVo", workCategoryVo);
//			
//			map.put("boardVo", boardVo);
//			
//			datalist.add(map);
//		}
//		
//		return datalist;
//	}
	
//	public ArrayList<HashMap<String, Object>> getMainBoardList(int recruitType_no,String project_state,int job_no){
//		
//		ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
//		
//		ArrayList<ProjectBoardVo> boardVoList = normalMapper.getMainBoardList(recruitType_no, project_state, job_no);
//		
//		for(ProjectBoardVo boardVo : boardVoList) {
//			
//			JobCategoryVo jobCategoryVo  = normalMapper.selectJobCategoryByProjectNo(boardVo.getProject_no());
//			
//			int rtCategoryNo = boardVo.getRecruitType_no();
//			RecruitTypeCategoryVo rtCategoryVo = normalMapper.getRecruitTypeByNo(rtCategoryNo);
//			
//			int cooCategoryNo = boardVo.getCooperation_no();
//			CooperationCategoryVo cooCategoryVo = normalMapper.getCooperationByNo(cooCategoryNo);
//			
//			int workCategoryNo = boardVo.getWorkType_no();
//			WorkTypeCategoryVo workCategoryVo = normalMapper.getWorkTypeByNo(workCategoryNo);
//			
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			
//			map.put("jobCategoryVo", jobCategoryVo);
//			map.put("rtCategoryVo", rtCategoryVo);
//			map.put("cooCategoryVo", cooCategoryVo);
//			map.put("workCategoryVo", workCategoryVo);
//			
//			map.put("boardVo", boardVo);
//			
//			datalist.add(map);
//		}
//		
//		return datalist;
//	}
	
	public ArrayList<HashMap<String, Object>> getSubAdProjectList() {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		
		ArrayList<ProjectBoardVo> subAdProjectVoList = normalMapper.selectSubAdProjectList();
		for(ProjectBoardVo subAdProjectVo : subAdProjectVoList) {
			HashMap<String, Object> map = new HashMap<>();
			
			int project_no = subAdProjectVo.getProject_no();
			
			map.put("subAdProjectVo", subAdProjectVo);
			map.put("jobCategoryVoList", normalMapper.selectJobCategoryByProjectNo(project_no));
			map.put("cooperationCategoryVo", normalMapper.selectCooperationCategoryByProjectNo(project_no));
			
			resultList.add(map);
		}
		
		return resultList;
	}	
}
