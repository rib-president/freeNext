package com.teamb.freenext.admin.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamb.freenext.admin.service.AdminService;
import com.teamb.freenext.biz.service.BizService;

@RestController
@RequestMapping("/admin/*")
public class RestAdminController {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private BizService bizService;
	
	@RequestMapping("getDashBoardData")
	public HashMap<String, Object> getDashBoardData() {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("memberProportion", adminService.getMemberProportion());				
		data.put("memberCntDuringWeek", adminService.getMemberCountDuringWeek());
		data.put("cooperationCntDuringWeek", adminService.getRegistProjectCountDuringWeek());
		data.put("cooperationList", adminService.getCooperationCategoryList());
		data.put("cooperationProportion", adminService.getCountGroupByCooperation());
		data.put("categoryProportion", adminService.getJobCategoryProportion());
		data.put("jobCategoryList", bizService.getJobCategoryList());
		data.put("totalAdPrice", adminService.getTotalAdPrice());
		data.put("adCountDuringWeek", adminService.getAdCountDuringWeek());
		
		return data;
	}
}
