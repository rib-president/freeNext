package com.teamb.freenext.normal.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.teamb.freenext.vo.CooperationCategoryVo;
import com.teamb.freenext.vo.JobCategoryVo;
import com.teamb.freenext.vo.LocalCategoryVo;
import com.teamb.freenext.vo.MemberVo;
import com.teamb.freenext.vo.MyScrapVo;
import com.teamb.freenext.vo.ProjectBoardVo;
import com.teamb.freenext.vo.RecruitTypeCategoryVo;
import com.teamb.freenext.vo.WorkTypeCategoryVo;

@Mapper
public interface NormalMapper {
	
	// 메인페이지에 출력되는 데이터 가져오기
	public int getTotalProjectCount();
	public int getTotalCustomerCount();
	public int getTotalCompanyCount();
	public ArrayList<ProjectBoardVo> selectHotProjectShuffle(int projectNum);
	public ArrayList<ProjectBoardVo> selectNewProject();
	
	// 프로젝트 리스트 가져오기
	public ArrayList<HashMap<String, Object>> selectProjectList(HashMap<String, Object> searchData);
	public int selectProjectListCount(HashMap<String, Object> searchData);
	
	// 관련 프로젝트 6개 가져오기(프로젝트 상세보기)
	public ArrayList<ProjectBoardVo> selectRelativeProjectShuffle(int[] job_no_list);
	public ArrayList<ProjectBoardVo> selectRelativeOSProjectShuffle();
	
	// 최근 본 글 가져오기
	public ProjectBoardVo selectSeenProject(int project_no);
	
	// 안 읽은 알람 개수
	public int selectUnReadAlarmCount(int member_no);
	
	// 희망직종/지역 가져오기
	public ArrayList<JobCategoryVo> selectDesiredJob(int member_no);
	public ArrayList<LocalCategoryVo> selectDesiredLocal(int member_no);
	
	// 알람 리스트 가져오기
	public ArrayList<HashMap<String, Object>> selectAlarmList(HashMap<String, Object> searchData);
	public int selectAlarmListCount(HashMap<String, Object> searchData);

	// 모든/특정 알람 읽음 처리
	public void updateReadAlarm(@Param("member_no") int member_no, @Param("project_no") int project_no);
	
	// 스크랩된 프로젝트 리스트
	public ArrayList<ProjectBoardVo>getScrapProjectList(@Param("member_no") int member_no, @Param("startNum") int startNum);
	
	// 스크랩 수
	public int getScrapCount(int member_no);
	
	// 메인페이지에 출력되는 데이터 가져오기(old)
	//public ArrayList<ProjectBoardVo> selectLatestProjectList();
	public ArrayList<JobCategoryVo> selectJobCategoryByProjectNo(int project_no);
	public CooperationCategoryVo selectCooperationCategoryByProjectNo(int project_no);
	
    //프리랜서 매칭, IT아웃소싱 상세 페이지 페이지 가져오기
	public ProjectBoardVo getProjectDetailPageByNo(int project_no) ;
	
	// 조회수 증가
	public void updateProjectReadCount(int project_no);
	
	// 광고 사용 여부
	public int isAdProject(int project_no);
	
	public ArrayList<MyScrapVo> getMyScrapListOrderByRegistDate(int member_no);
	
	//스크랩 하기
	public void doScrap(MyScrapVo vo);
	public void deleteScrap(MyScrapVo vo);
	public int getTotalScrapCount(int project_no);
	public int getMyScrapCount(MyScrapVo vo);
	
	// 프로젝트 리스트에서 스크랩 삭제
	public void deleteMyScrapByList(int scrap_no);
	
	// 내 프로젝트 리스트 가져오기
	public ArrayList<ProjectBoardVo> selectMyProjectList(HashMap<String, Object> searchData);
	public int selectMyProjectListCount(HashMap<String, Object> searchData);
	public ArrayList<ProjectBoardVo> selectMyProjectListAll(int member_no);
	
	// 프로젝트 상태 변경
	public void updateProjectState(@Param("project_no") int project_no, @Param("project_state") String project_state);
	
	//지역 가져오기
	public LocalCategoryVo getLocalByNo(int project_no);

	// 멤버정보 가져오기(customer/company)
	public HashMap<String, Object> selectMemberInfo(@Param("member_no") int member_no, @Param("member_type") String member_type);
	
	//회원정보 가져오기
	public MemberVo getMemberByNo(int member_no);
	//모집형태 가져오기
	public RecruitTypeCategoryVo getRecruitTypeByNo(int recruitType_no);
	//근무형태 가져오기
	public WorkTypeCategoryVo getWorkTypeByNo(int workType_no);
	//협력사 가져오기
	public CooperationCategoryVo getCooperationByNo(int cooperation_no);
	
	// 프로젝트 리스트 가져오기
	/*public ArrayList<ProjectBoardVo> getProjectBoardList(
			@Param("recruitType_no") int recruitType_no, 
			@Param("pageNum") int pageNum, 
			@Param("project_state") String project_state);*/
	
	// 프로젝트 리스트 가져오기
	public ArrayList<ProjectBoardVo> getProjectBoardList(
			int recruitType_no, 
			@Param("pageNum") int pageNum, 
			String project_state,
			int job_no);	
	
	public ArrayList<ProjectBoardVo> selectSubAdProjectList();
	
	/*public int getProjectBoardCount(@Param("recruitType_no") int recruitType_no,
									@Param("project_state") String project_state);*/
	
	public int getProjectBoardCount(int recruitType_no,String project_state,int job_no);
	
	public ArrayList<ProjectBoardVo> getHotBoardList(
			int recruitType_no,
			String project_state,
			int job_no);
	
	public ArrayList<ProjectBoardVo> getMainBoardList(
			int recruitType_no,
			String project_state,
			int job_no);	
}
