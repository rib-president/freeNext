package com.teamb.freenext.biz.service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamb.freenext.biz.mapper.BizMapper;
import com.teamb.freenext.member.mapper.MemberMapper;
import com.teamb.freenext.normal.mapper.NormalMapper;
import com.teamb.freenext.scraping.mapper.ScrapingMapper;
import com.teamb.freenext.vo.AdVo;
import com.teamb.freenext.vo.JobCategoryVo;
import com.teamb.freenext.vo.ProjectBoardVo;
import com.teamb.freenext.vo.ProjectJobVo;
import com.teamb.freenext.vo.ProjectLocalVo;
import com.teamb.freenext.vo.RecruitTypeCategoryVo;
import com.teamb.freenext.vo.WorkTypeCategoryVo;

@Service
public class BizService {
	
	@Autowired
	private BizMapper bizMapper;
	
	@Autowired
	private NormalMapper normalMapper;
	
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
	private ScrapingMapper scrapingMapper;
	
	public int registProjectProcess(ProjectBoardVo vo, int[] job_no_list, int[] local_no_list, String subAd) {
		bizMapper.insertProject(vo);
		int project_no = memberMapper.getLastPK();
		
		if(job_no_list != null) {

			for(int job_no : job_no_list) {
				if(job_no == 0) continue;
				ProjectJobVo projectJobVo = new ProjectJobVo();
				projectJobVo.setProject_no(project_no);
				projectJobVo.setJob_no(job_no);
				
				bizMapper.insertProjectJob(projectJobVo);
			}			
		}
		
		if(local_no_list != null) {
			
			for(int local_no : local_no_list) {
				if(local_no == 0) continue;
				ProjectLocalVo projectLocalVo = new ProjectLocalVo();
				projectLocalVo.setProject_no(project_no);
				projectLocalVo.setLocal_no(local_no);
				
				bizMapper.insertProjectLocal(projectLocalVo);
			}
		}
		
		if(subAd != null) {
			subAd(project_no);
		}
		
		return project_no;
	}
	
	public ArrayList<JobCategoryVo> getJobCategoryList() {
		return bizMapper.selectJobCategory();
	}
	
	public ArrayList<WorkTypeCategoryVo> getWorkTypeCateogoryList() {
		return bizMapper.selectWorkTypeCategory();
	}
	
	public ArrayList<RecruitTypeCategoryVo> getRecruitTypeCategoryList() {
		return bizMapper.selectRecruitTypeCategory();
	}
	
	public int getProjectListCountByMemberNo(int member_no, String searchOption, String searchKeyword,
											int groupOne, int groupTwo, String state_cdT) {
		return bizMapper.selectProjectListCountByMemberNo(
				member_no, searchOption, searchKeyword, groupOne, groupTwo, state_cdT);
	}
 
	public ArrayList<HashMap<String, Object>> getProjectListByMemberNo(int member_no, String searchOption, String searchKeyword,
											int groupOne, int groupTwo, String state_cdT, int startNum)
	{

		ArrayList<ProjectBoardVo> projectBoardVoList = bizMapper.selectProjectListByMemberNo(
				member_no, searchOption, searchKeyword, groupOne, groupTwo, state_cdT, startNum);

		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		for(ProjectBoardVo projectBoardVo : projectBoardVoList) {
			HashMap<String, Object> map = new HashMap<>();
			
			map.put("projectBoardVo", projectBoardVo);				
			map.put("recruitTypeCategoryVo", normalMapper.getRecruitTypeByNo(projectBoardVo.getRecruitType_no()));
			map.put("jobCategoryVo", normalMapper.selectJobCategoryByProjectNo(projectBoardVo.getProject_no()));
			
			resultList.add(map);
		}
		return resultList;
	}
	
	public void modifyProjectProcess(HashMap<String, Object> receivedData) {
		
		bizMapper.updateProject(receivedData);
		int project_no = Integer.valueOf(String.valueOf(receivedData.get("project_no")));
		
		int job_no = Integer.valueOf(String.valueOf(receivedData.get("job_no_list")));
		
		if(normalMapper.selectJobCategoryByProjectNo(project_no).get(0).getJob_no() != job_no) {
			
			bizMapper.deleteProjectJob(project_no);
			
			ProjectJobVo projectJobVo = new ProjectJobVo();
			projectJobVo.setProject_no(project_no);
			projectJobVo.setJob_no(job_no);
			
			bizMapper.insertProjectJob(projectJobVo);
		}
		
		int local_no = Integer.valueOf(String.valueOf(receivedData.get("local_no_list")));
		if(normalMapper.getLocalByNo(project_no).getLocal_no() != local_no) {
			
			bizMapper.deleteProjectLocal(project_no);
			
			ProjectLocalVo projectLocalVo = new ProjectLocalVo();
			projectLocalVo.setProject_no(project_no);
			projectLocalVo.setLocal_no(local_no);			
						
			bizMapper.insertProjectLocal(projectLocalVo);			
		}
				
		if(receivedData.get("subAd") != null) {			
			subAd(project_no);
		}		
	}
	
	public void subAd(int project_no) {
		AdVo adVo = new AdVo();
		adVo.setProject_no(project_no);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, 7);
		adVo.setAd_endDate(date.getTime());
		
		bizMapper.insertAd(adVo);
	}
	
	public AdVo getProjectAd(int project_no) {
		return bizMapper.selectAdByProjectNo(project_no);
	}
	
	
	public void deleteBoard(int project_no)
	{
		bizMapper.deleteProject(project_no);
		bizMapper.deleteProjectJob(project_no);
		bizMapper.deleteProjectLocal(project_no);
		bizMapper.deleteProjectAd(project_no);
		bizMapper.deleteProjectScrap(project_no);
		bizMapper.deleteProjectAlarm(project_no);
	}
	
	public ArrayList<HashMap<String, Object>> getMyProjectScrapCount(int member_no) {
		ArrayList<Integer> adProjectNoList = bizMapper.selectMyAdProject(member_no);
		
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		
		ArrayList<String> weekList = getWeekList();
		
		for(ProjectBoardVo projectVo : normalMapper.selectMyProjectListAll(member_no)) {
			HashMap<String, Object> map = new HashMap<>();						
			
			int project_no = projectVo.getProject_no();
			
			map.put("project_name", projectVo.getProject_name());
			if(adProjectNoList.contains(project_no)) {
				map.put("isAd", true);
			} else {
				map.put("isAd", false);
			}
			
			ArrayList<HashMap<String, Object>> getCountList = bizMapper.selectMyProjectScrapCount(project_no);
			List<String> scrapDateList = getCountList.stream().map(e -> e.get("scrapDate").toString()).collect(Collectors.toList());
			
			ArrayList<HashMap<String, Object>> scrapCountList = new ArrayList<>();
			for(String day : weekList) {
				HashMap<String, Object> addMap = new HashMap<>();					
				
				addMap.put("project_name", projectVo.getProject_name());
				addMap.put("scrapDate", day);
				
				if(scrapDateList.contains(day)) {
					for(HashMap<String, Object> getCountMap : getCountList) {
						if(getCountMap.get("scrapDate").toString().equals(day.toString())) {

							addMap.put("cnt", getCountMap.get("cnt"));
							continue;
						}	
					}
				} else {
					addMap.put("cnt", 0);
				}
				scrapCountList.add(addMap);
			}

			map.put("scrapCountList", scrapCountList);
			
			resultList.add(map);
		}
		
		return resultList;
	}
	
	public ArrayList<HashMap<String, Object>> getMyProjectListAll(int member_no) {
		ArrayList<Integer> adProjectNoList = bizMapper.selectMyAdProject(member_no);
		
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		for(ProjectBoardVo projectBoardVo : normalMapper.selectMyProjectListAll(member_no)) {
			HashMap<String, Object> map = new HashMap<>();
			
			map.put("projectVo", projectBoardVo);
			if(adProjectNoList.contains(projectBoardVo.getProject_no())) {
				map.put("isAd", true);
			} else {
				map.put("isAd", false);
			}
			
			resultList.add(map);
		}
		
		return resultList;
	}
	
	public ArrayList<String> getWeekList() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<String> weekList = new ArrayList<>();
		for(int i=6;i>=0;i--) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE,  -i);
			weekList.add(sdf.format(cal.getTime()));
		}
		return weekList;
	}
	
	public String getKakaoKey() {
		return scrapingMapper.selectKakaoKey();
	}
	
}
