package com.teamb.freenext.member.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.freenext.member.service.MemberService;
import com.teamb.freenext.vo.MemberCompanyVo;
import com.teamb.freenext.vo.MemberCustomerVo;
import com.teamb.freenext.vo.MemberVo;

@RestController
@RequestMapping("/member/*")
public class RestMemberController {
	
	@Autowired
	private MemberService memberService;
	
	@RequestMapping("isExistId")
	public HashMap<String, Object> isExistId(String member_id) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		boolean result = memberService.isExistId(member_id);
		
		data.put("result", result);
		
		return data;
		
	}
	
	@RequestMapping("getJSKey")
	public HashMap<String, Object> getJSKey() {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("jsKey", memberService.getJSKey());
		
		return data;
	}

	
	@RequestMapping("changePassword")
	public HashMap<String, Object> changePassword(@RequestBody HashMap<String, String> receivedData, HttpSession session) {
		HashMap<String, Object> data = new HashMap<>();
		
		memberService.changePassword(receivedData);
				
		MemberVo sessionUser = (MemberVo) session.getAttribute("sessionUser");
		
		if(sessionUser != null) {
			sessionUser.setMember_pw(receivedData.get("member_pw"));
			
			session.setAttribute("sessionUser", sessionUser);
			data.put("sessionUser", sessionUser);			
		} else {
			MemberVo sessionBizUser = (MemberVo) session.getAttribute("sessionBizUser");
			sessionBizUser.setMember_pw(receivedData.get("member_pw"));
			
			session.setAttribute("sessionBizUser", sessionBizUser);
			data.put("sessionBizUser", sessionBizUser);
		}
		
		data.put("result", "success");

		
		return data;
	}
	
	@RequestMapping("modifyCustomerProcess")
	public HashMap<String, Object> modifyMemberProcess(MemberVo memberVo, MemberCustomerVo customerVo, 
														int[] job_no_list, int[] local_no_list,
														MultipartFile uploadFileThumbnail, HttpSession session) {
		HashMap<String, Object> data = new HashMap<>();	
		
		if(uploadFileThumbnail != null && !uploadFileThumbnail.isEmpty()) {
			String uploadFolder = "C:/freeNext/profileImage/";

			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
			String folderPath = sdf.format(today);
			
			File todayFolder = new File(uploadFolder + folderPath);
			
			if(!todayFolder.exists()) {
				todayFolder.mkdirs();
			}
			
			// 중복되지않게 저장
			String fileName = "";
			UUID uuid = UUID.randomUUID();
			fileName += uuid.toString();
			
			long currentTime = System.currentTimeMillis();
			fileName += "_" + currentTime;			
			
			if(uploadFileThumbnail != null && !uploadFileThumbnail.isEmpty()) {
				try {
					//확장자 추가
					String originalFileName = uploadFileThumbnail.getOriginalFilename();
					String ext = originalFileName.substring(originalFileName.lastIndexOf("."));				
					
					uploadFileThumbnail.transferTo(new File(uploadFolder + folderPath + fileName + ext));
					
					memberVo.setMember_profile(folderPath + fileName + ext);
				} catch(Exception e) {
					System.out.println("이미지 저장 익셉션]" + e.getMessage());
				}				
			}
		} else {
			memberVo.setMember_profile(((MemberVo) session.getAttribute("sessionUser")).getMember_profile());
		}
		
		memberService.modifyCustomerProcess(memberVo, customerVo, job_no_list, local_no_list);
		
		MemberVo sessionUser = memberService.getMemberByNo(memberVo.getMember_no()); 
		
		session.setAttribute("sessionUser", sessionUser);
		data.put("sessionUser", sessionUser);
		
		return data;
	}
	
	@RequestMapping("modifyCompanyrProcess")
	public HashMap<String, Object> modifyCompanyrProcess(MemberVo memberVo, MemberCompanyVo companyVo, String old_license_img,
									MultipartFile uploadFileThumbnail, MultipartFile uploadFileBizImg, HttpSession session) {
		HashMap<String, Object> data = new HashMap<>();
		
		if((uploadFileThumbnail != null && !uploadFileThumbnail.isEmpty()) || 
				(uploadFileBizImg != null && !uploadFileBizImg.isEmpty())) {
			String uploadFolder = "C:/freeNext/profileImage/";

			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
			String folderPath = sdf.format(today);
			
			File todayFolder = new File(uploadFolder + folderPath);
			
			if(!todayFolder.exists()) {
				todayFolder.mkdirs();
			}
			
			// 중복되지않게 저장
			String fileName = "";
			UUID uuid = UUID.randomUUID();
			fileName += uuid.toString();
			
			long currentTime = System.currentTimeMillis();
			fileName += "_" + currentTime;			
			
			if(uploadFileThumbnail != null && !uploadFileThumbnail.isEmpty()) {
				try {
					//확장자 추가
					String originalFileName = uploadFileThumbnail.getOriginalFilename();
					String ext = originalFileName.substring(originalFileName.lastIndexOf("."));				
					
					uploadFileThumbnail.transferTo(new File(uploadFolder + folderPath + fileName + ext));
					
					memberVo.setMember_profile(folderPath + fileName + ext);
				} catch(Exception e) {
					System.out.println("이미지 저장 익셉션]" + e.getMessage());
				}				
			}
			
			if(uploadFileBizImg != null && !uploadFileBizImg.isEmpty()) {
				try {
					//확장자 추가
					String originalFileName = uploadFileBizImg.getOriginalFilename();
					String ext = originalFileName.substring(originalFileName.lastIndexOf("."));			
					
					uploadFileBizImg.transferTo(new File(uploadFolder + folderPath + companyVo.getCompany_license_number() + "_" + fileName + ext));
					companyVo.setCompany_license_img(folderPath + companyVo.getCompany_license_number() + "_" + fileName + ext);
				} catch(Exception e) {
					System.out.println("사업자 등록증 저장 익셉션]" + e.getMessage());
				}					
			}			
		}
		
		if(uploadFileThumbnail == null || uploadFileThumbnail.isEmpty()) {
			memberVo.setMember_profile(((MemberVo) session.getAttribute("sessionBizUser")).getMember_profile());
		}
		
		if(uploadFileBizImg == null || uploadFileBizImg.isEmpty()) {
			companyVo.setCompany_license_img(old_license_img);
		}
		
		memberService.modifyCompanyProcess(memberVo, companyVo);
		
		MemberVo sessionBizUser = memberService.getMemberByNo(memberVo.getMember_no()); 
		
		session.setAttribute("sessionBizUser", sessionBizUser);
		data.put("sessionBizUser", sessionBizUser);		
		
		return data;
	}
}
