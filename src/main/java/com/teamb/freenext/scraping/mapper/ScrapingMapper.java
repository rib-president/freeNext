package com.teamb.freenext.scraping.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import com.teamb.freenext.vo.ProjectJobVo;
import com.teamb.freenext.vo.ProjectLocalVo;

@Mapper
public interface ScrapingMapper {
	public int isProjectExist(String project_key);
	public String selectStartKey(int cooperation_no);
	public ArrayList<String> selectEndKeyElancer(int job_no);
	public ArrayList<String> selectEndKeyWishket();
	public ProjectJobVo selectProjectJobByProjectNo(int project_no);
	public ProjectLocalVo selectProjectLocalByProjectNo(int project_no);
	public void insertOrUpdate(HashMap<String, Object> data);
	public void alarmInsert(HashMap<String, Object> data);
	public String selectKakaoKey();
	public ArrayList<Integer> selectJobAlarmMember(int job_no);
	public ArrayList<Integer> selectLocalAlarmMember(int local_no);
	public int countDesiredLocalOfMember(int member_no);
	public int countDesiredJobOfMember(int member_no);
}
