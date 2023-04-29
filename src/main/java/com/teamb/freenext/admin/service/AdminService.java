package com.teamb.freenext.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamb.freenext.admin.mapper.AdminMapper;
import com.teamb.freenext.biz.service.BizService;
import com.teamb.freenext.vo.AdminVo;
import com.teamb.freenext.vo.CooperationCategoryVo;
import com.teamb.freenext.vo.MemberCompanyVo;
import com.teamb.freenext.vo.MemberCustomerVo;
import com.teamb.freenext.vo.MemberVo;

@Service
public class AdminService {
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private BizService bizService;
	
	public AdminVo adminLogin(AdminVo vo) {
		
		AdminVo result = adminMapper.getAdminByIdAndPw(vo);
		
		return result;
	}
	
	
	public ArrayList<MemberVo> getMemberList(HashMap<String, Object> map){

		return adminMapper.getMemberList(map);
		
	}	
	
	public HashMap<String, Object> getMember (int memberNo){
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		MemberVo memberVo = adminMapper.getCommonMemberByNo(memberNo);
		//memberNo = memberVo.getMember_no();
		map.put("memberVo", memberVo);
		
		if(memberVo.getMember_type().equals("N")) {
			
			MemberCustomerVo memberCustomerVo = adminMapper.getMemberCustomerByNo(memberNo);
			//int memberCustomerNo = memberCustomerVo.getCustomer_no();
			map.put("memberCustomerVo", memberCustomerVo);
			map.put("customer_birth", memberCustomerVo.getCustomer_birth());
			map.put("memberCompanyVo", null);
			
		} else {
			MemberCompanyVo memberCompanyVo = adminMapper.getMemberCompanyByNo(memberNo);
			//int memberCompanyNo = memberCompanyVo.getCompany_no();
			map.put("memberCompanyVo", memberCompanyVo);
			map.put("customer_birth", null);
			
		}
		
		return map;
				
	}
	
	public void updateMemberN(MemberVo mvo, MemberCustomerVo cvo) {
		adminMapper.updateCommonMember(mvo);
		adminMapper.updateMemberCustomer(cvo);
		
	}
	
	public void updateMemberB(MemberVo mvo, MemberCompanyVo cvo) {
		adminMapper.updateCommonMember(mvo);
		adminMapper.updateMemberCompany(cvo);
	}

	public int getTotalMemberNumber(HashMap<String, Object> map) {
				
		int result = adminMapper.getTotalNumber(map);
		
		return result;
	}
	
	public int getToalMemberTypeNumber(HashMap<String, Object> map, String member_type) {
		
		map.put("member_type", member_type);
		
		int result = adminMapper.getTotalMemberTypeNumber(map);
		
		return result;
	}
	
	public int getCompanyNumber() {
		
		int result = adminMapper.getTotalCompany();
		
		return result;
	}
	
	public MemberCompanyVo getCompanyVo(int member_no) {
		return adminMapper.selectCompanyVoByMemberNo(member_no);
	}
	
	// 통계
	// 회원 비율
	public HashMap<String, Object> getMemberProportion() {
		ArrayList<HashMap<String, String>> memberTypeCountList = adminMapper.selectAllMemberType();

		HashMap<String, Object> resultMap = new HashMap<>();
		
		for(HashMap<String, String> memberTypeCountMap : memberTypeCountList) {
			if(String.valueOf(memberTypeCountMap.get("member_type")).equals("N")) {
				resultMap.put("normalProportion", memberTypeCountMap.get("cnt"));
			} else {
				resultMap.put("bizProportion", memberTypeCountMap.get("cnt"));
			}
		}

		return resultMap;
	}
	
	// 일별 회원가입수(최근 일주일)
	public ArrayList<HashMap<String, Object>> getMemberCountDuringWeek() {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		
		ArrayList<String> weekList = bizService.getWeekList();
		
		ArrayList<HashMap<String, String>> joinDateDuringWeekList = adminMapper.selectJoinDateDuringWeek(weekList.get(0), weekList.get(6));
		List<String> joinDateList = joinDateDuringWeekList.stream().map(e -> String.valueOf((Object) e.get("joinDate"))).collect(Collectors.toList());

		for(String week : weekList) {
			HashMap<String, Object> map = new HashMap<>();
			
			if(joinDateList.contains(week)) {
				for(HashMap<String, String> memberCountMap : joinDateDuringWeekList) {
					if(String.valueOf((Object) memberCountMap.get("joinDate")).equals(week)) {
						map.put("day", week);
						map.put("cnt", memberCountMap.get("cnt"));
						
						resultList.add(map);
						break;	
					}					
				}				
			} else {
				map.put("day", week);
				map.put("cnt", 0);
				
				resultList.add(map);
			}
		}

		return resultList;
	}
	
	// 일별  등록주체 프로젝트 등록/스크래핑 수(최근 일주일)
	public ArrayList<HashMap<String, Object>> getRegistProjectCountDuringWeek() {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		
		ArrayList<String> weekList = bizService.getWeekList();
		for(int i=1;i<6;i++) {
			ArrayList<HashMap<String, String>> registProjectCntList = adminMapper.selectRegistProjectCntDuringWeek(i, weekList.get(0), weekList.get(weekList.size()-1));
			List<String> registDateList = registProjectCntList.stream().map(e -> String.valueOf((Object) e.get("project_date"))).collect(Collectors.toList());
			
			HashMap<String, Object> resultMap = new HashMap<>();
			ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();			
			
			for(String week : weekList) {
				HashMap<String, Object> map = new HashMap<>();
				
				if(registDateList.contains(week)) {
					for(HashMap<String, String> cntCountMap : registProjectCntList) {
						if(String.valueOf((Object) cntCountMap.get("project_date")).equals(week)) {
							map.put("day", week);
							map.put("cnt", cntCountMap.get("cnt"));
							map.put("cooperation_no", i);
							
							mapList.add(map);
							
							break;
						}
					}
				} else {
					map.put("day", week);
					map.put("cnt", 0);
					map.put("cooperation_no", i);
					
					mapList.add(map);
				}
			}
			
			resultMap.put("mapList", mapList);
			resultList.add(resultMap);
		}
		
		return resultList;
	}
	
	public ArrayList<CooperationCategoryVo> getCooperationCategoryList() {
		return adminMapper.selectCooperationCategoryList();
	}
	
	// 개별등록/스크래핑 비율
	public ArrayList<HashMap<String, Object>> getCountGroupByCooperation() {

		return adminMapper.selectCountGroupByCooperation();
	}
	
	// 잡 카테고리별 프로젝트 비율
	public ArrayList<HashMap<String, String>> getJobCategoryProportion() {

		return adminMapper.selectJobCategoryProportion();
	}
	
	// 누적 광고수익
	public int getTotalAdPrice() {
		return adminMapper.selectTotalAdPrice();
	}
	
	// 일별 등록 광고(최근 일주일)
	public ArrayList<HashMap<String, Object>> getAdCountDuringWeek() {
		ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
		
		ArrayList<String> weekList = bizService.getWeekList();
		
		ArrayList<HashMap<String, String>> adCountList = adminMapper.selectAdCountDuringWeek(weekList.get(0), weekList.get(weekList.size()-1));
		List<String> registDateList = adCountList.stream().map(e -> String.valueOf((Object) e.get("registDate"))).collect(Collectors.toList());
		for(String week : weekList) {
			HashMap<String, Object> map = new HashMap<>();
			
			if(registDateList.contains(week)) {
				for(HashMap<String, String> adCountMap : adCountList) {
					if(String.valueOf((Object) adCountMap.get("registDate")).equals(week)) {
						map.put("cnt", adCountMap.get("cnt"));
						map.put("registDate", week);
						
						resultList.add(map);
						
						break;
					}
				}
			} else {
				map.put("cnt", 0);
				map.put("registDate", week);
				
				resultList.add(map);
			}
		}
		
		return resultList;
	}
}
