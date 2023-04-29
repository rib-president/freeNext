package com.teamb.freenext.admin.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.teamb.freenext.vo.AdminVo;
import com.teamb.freenext.vo.CooperationCategoryVo;
import com.teamb.freenext.vo.MemberCompanyVo;
import com.teamb.freenext.vo.MemberCustomerVo;
import com.teamb.freenext.vo.MemberVo;

@Mapper
public interface AdminMapper {
	
	public AdminVo getAdminByIdAndPw(AdminVo vo);
	
	public ArrayList<MemberVo> getMemberList(HashMap<String, Object> map);	

	public MemberVo getCommonMemberByNo(int no);
	
	public MemberCustomerVo getMemberCustomerByNo(int no);
	
	public MemberCompanyVo getMemberCompanyByNo(int no);

	public void updateCommonMember(MemberVo vo);
	
	public void updateMemberCustomer(MemberCustomerVo vo);
	
	public void updateMemberCompany(MemberCompanyVo vo);
	
	public int getTotalNumber(HashMap<String, Object> map);
	
	public int getTotalMemberTypeNumber(HashMap<String, Object> map);
	
	public int getTotalCompany();
	
	public MemberCompanyVo selectCompanyVoByMemberNo(int member_no);
	
	public ArrayList<CooperationCategoryVo> selectCooperationCategoryList();
	
	public ArrayList<HashMap<String, String>> selectAllMemberType();
	public ArrayList<HashMap<String, String>> selectJoinDateDuringWeek(@Param("startDate") String startDate, @Param("endDate") String endDate);
	public ArrayList<HashMap<String, String>> selectRegistProjectCntDuringWeek(@Param("cooperation_no") int cooperation_no, @Param("startDate") String startDate, @Param("endDate") String endDate);
	public ArrayList<HashMap<String, Object>> selectCountGroupByCooperation();
	public ArrayList<HashMap<String, String>> selectJobCategoryProportion();
	public int selectTotalAdPrice();
	public ArrayList<HashMap<String, String>> selectAdCountDuringWeek(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
