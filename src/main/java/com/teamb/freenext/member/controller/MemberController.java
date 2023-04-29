package com.teamb.freenext.member.controller;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.teamb.freenext.commons.KakaoRestAPI;
import com.teamb.freenext.member.service.MemberService;
import com.teamb.freenext.scraping.service.ScrapingService;
import com.teamb.freenext.vo.MemberCompanyVo;
import com.teamb.freenext.vo.MemberCustomerVo;
import com.teamb.freenext.vo.MemberVo;

@Controller
@RequestMapping("/member/*")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ScrapingService scrapingService;
	
	@RequestMapping("loginPage")
	public String loginPage() {
		
		return "member/loginPage";
	}
	
	@RequestMapping("loginGetCode")
	public String loginGetCode(String code, Model model) {		
		
		KakaoRestAPI kakao = new KakaoRestAPI();		
					
		Map<String, Object> result = kakao.loginGetToken(code, scrapingService.getKakaoKey());
		
		model.addAttribute("access_token", result.get("access_token"));
		model.addAttribute("expires_in", result.get("expires_in"));
		model.addAttribute("refresh_token", result.get("refresh_token"));
		model.addAttribute("jsKey", memberService.getJSKey());
		
		return "member/getKakaoUserInfo";
	}
	
	@RequestMapping("checkJoin")
	public String checkJoin(String member_id, String member_email, String member_name, String member_profile,
			HttpSession session, Model model) {
		MemberVo vo = memberService.isExist(member_id);
		
		if(vo != null) {
			session.setAttribute("sessionUser", vo);
			return "redirect:../normal/main";
		} else {
			model.addAttribute("customer_kakao", "Y");
			model.addAttribute("customer_kakao_id", member_id);
			model.addAttribute("customer_kakao_email", member_email);
			model.addAttribute("customer_kakao_name", member_name);
			model.addAttribute("customer_kakao_profile", member_profile);
			
			model.addAttribute("jobList", memberService.getJobCategoryList());
			model.addAttribute("localList", memberService.getLocalCategoryList());
			
			return "member/joinMemberPage";

		}
	}	
	
	@RequestMapping("joinMemberPage")
	public String joinMemberPage(Model model) {		
		
		model.addAttribute("jobList", memberService.getJobCategoryList());
		model.addAttribute("localList", memberService.getLocalCategoryList());
		
		return "member/joinMemberPage";
	}
	
	@RequestMapping("findIdPage")
	public String findIdPage() {
		
		
		return "member/findIdPage";
	}
	
	@RequestMapping("findPwPage")
	public String findPwPage(MemberVo mvo, Model model) {
		
		model.addAttribute("mvo", mvo);
		
		return "member/findPwPage";
	}
	
	@RequestMapping("findIdResultPage")
	public String findIdResultPage(MemberVo mvo, Model model) {
		
		MemberVo idInfo = memberService.findId(mvo);
		if(idInfo == null || !idInfo.getMember_type().equals("N")) {
			return "member/failFindId";
		}
		
		int countId = memberService.getCountFindId(mvo);
		
		model.addAttribute("countId", countId);
		model.addAttribute("idInfo", idInfo);
		
		if(countId == 0) {
			return "member/failFindId";
		} else {
			return "member/findIdResultPage";
		}
		
		
	}
	
	@RequestMapping("findPwResultPage")
	public String findPwResultPage(MemberVo mvo, Model model) {
		
		MemberVo vo = memberService.findPw(mvo);
		
		
		
		if(vo == null || !vo.getMember_type().equals("N")) {
			
			return "member/failFindPw";
			
		} else {
			
			MemberVo getEmail = memberService.findId(mvo);
			String member_email = getEmail.getMember_email();
			
			//임시Pw 생성
			String temporaryPw = UUID.randomUUID().toString().replaceAll("-", "");
			temporaryPw = temporaryPw.substring(0, 12);
			System.out.println(temporaryPw);
			
			//임시Pw 셋팅
			mvo.setMember_pw(temporaryPw);
			mvo.setMember_email(member_email);
			
			//기존Pw 에서 임시Pw로 변경
			memberService.changeToTemporaryPw(mvo);
			//임시Pw 메일발송
			memberService.temporaryPwSendMail(mvo);
			
			MemberVo memberVo = memberService.getMemberInfo(mvo);
			model.addAttribute("memberVo", memberVo);
			
			return "member/findPwResultPage";
		}
		
		
	}
	
	
	@RequestMapping("memberDuplicationCheckPage")
	public String memberDuplicationCheckPage() {
		
		return "member/memberDuplicationCheckPage";
	}
	
	@RequestMapping("duplicationCheckProcess")
	public String duplicationCheckProcess(MemberVo mvo) {
		
		int duplicationCount = memberService.isExistMember(mvo);
		
		if(duplicationCount == 0) {
			return "redirect:./joinMemberPage";
		} else {
			return "member/memberExist";
		}
		
	}	
	
	@RequestMapping("loginProcess")
	public String loginProcess(MemberVo vo, HttpSession session, Model model, HttpServletRequest request) {				

		MemberVo memberVo = memberService.login(vo, true);
		
		if(memberVo != null) {
			if(memberVo.getMember_type().equals("N")) {
				session.setAttribute("sessionUser", memberVo);								
			} else if(memberVo.getMember_type().equals("B")){
				session.setAttribute("sessionBizUser", memberVo);				
			}
			return "redirect:../normal/main";
		}		
		return "member/loginFail";		
	}	
	
	@RequestMapping("mailAuthProcess")
	public String mailAuthProcess(String authKey) {
		
		memberService.authMail(authKey);
		
		return "member/mailAuthComplete";
		
	}	
	
	@RequestMapping("logoutProcess")
	public String logoutProcess(HttpServletRequest request, HttpSession session) {
		
		session.removeAttribute("sessionUser");
		session.removeAttribute("sessionBizUser");
		
		return "redirect:../normal/main";
	}	
	
	@RequestMapping("joinMemberProcess")
	public String joinMemberProcess(MemberVo memberVo, MemberCustomerVo customerVo, MemberCompanyVo companyVo,
									String kakao_profile, int[] job_no_list, int[] local_no_list, 
									MultipartFile uploadFileThumbnail, MultipartFile uploadFileBizImg) {				
		
		String uploadFolder = "C:/freeNext/profileImage/";		
		
		if((uploadFileThumbnail != null && !uploadFileThumbnail.isEmpty()) || 
				(uploadFileBizImg != null && !uploadFileBizImg.isEmpty()) || (kakao_profile != null && !kakao_profile.equals(""))) {
		
			//날짜별 폴더 생성 2022/01/19/
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
			
			if(kakao_profile != null && !kakao_profile.equals("")) {  
				try(InputStream in = new URL(kakao_profile).openStream()){ 
					Path imagePath = Paths.get(uploadFolder + folderPath + fileName + ".jpg");
					Files.copy(in, imagePath); 
					memberVo.setMember_profile(folderPath + fileName + ".jpg");
				} catch (Exception e) {
					System.out.println("url다운로드&저장 익셉션] " + e.getMessage());
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

		memberService.joinMember(memberVo, customerVo, companyVo, job_no_list, local_no_list);
				
		return "member/joinMemberComplete";
	}


	
	@RequestMapping("bizLoginPage")
	public String bizLoginPage() {
		
		return "member/bizLoginPage";
	}
	
	@RequestMapping("bizSignUpPage")
	public String bizSignUpPage() {
		
		return "member/bizSignUpPage";
	}
	
	@RequestMapping("bizSignUpProcess")
	public String bizSignUpProcess(MemberVo param1, MemberCompanyVo param2,
			Model model, MultipartFile license_img, MultipartFile profile) {
		
		
		
		String uploadFolder = "C:/freenext/profileImage/"; 

		if(!license_img.isEmpty() && !profile.isEmpty()) {
			
			//날짜별 폴더 생성... 2022/01/19/
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/"); 
			String folderPath = sdf.format(today);
			
			File todayFolder = new File(uploadFolder + folderPath); 
			
			String imgName = "";
			String logoName = "";
			
			if(!todayFolder.exists()) {
				
				todayFolder.mkdirs();
			}
			
			long currentTime = System.currentTimeMillis();
//				파일 원래이름
//			String oriImgName = param2.getCompany_license_img();
//			String oriLogoName = param1.getMember_profile();
			
			String oriImgName = license_img.getOriginalFilename();
			String oriLogoName = profile.getOriginalFilename();
	
			// random uuid + 현재시간 + 확장자
			UUID uuid = UUID.randomUUID();
			imgName += (uuid.toString() + currentTime + oriImgName.substring(oriImgName.lastIndexOf(".")));
			uuid = UUID.randomUUID();
			logoName += (uuid.toString() + currentTime + oriLogoName.substring(oriLogoName.lastIndexOf(".")));
			
			try {
				license_img.transferTo(new File(uploadFolder + folderPath + imgName));
				profile.transferTo(new File(uploadFolder + folderPath + logoName));
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			param2.setCompany_license_img(folderPath + imgName);
			param1.setMember_profile(folderPath + logoName);
		}

		memberService.insertMember(param1);
		memberService.insertBizMember(param2);
		model.addAttribute("MemberVo", param1);
		model.addAttribute("MemberCompanyVo", param2);
		return "member/bizSignUpCompletePage";
	}
	
	@RequestMapping("bizSearchIdPage")
	public String bizSearchIdPage() {
		
		return "member/bizSearchIdPage";
	}
	@RequestMapping("bizCheckIdFailPage")
	public String bizCheckIdFailPage() {
		
		return "member/bizCheckIdFailPage";
	}
	
	@RequestMapping("bizSearchIdProcess")
	public String bizSearchIdProcess(MemberVo param, Model model) {
		MemberVo memberVo = memberService.getBizIdCheck(param);
		
		if(memberVo != null) {
			model.addAttribute("MemberVo", memberVo);
			return "member/bizCheckedIdPage";
		}else {
			return "member/bizCheckIdFailPage";
		}
	}
	
	@RequestMapping("bizSearchPwPage")
	public String bizSearchPwPage() {
		
		return "member/bizSearchPwPage";
	}
	
	
	@RequestMapping("bizSearchPwProcess")
	public String bizSearchPwProcess(MemberVo param, Model model) {
		 
		MemberVo memberVo = memberService.getBizPwCheck(param);
		//기존 비밀번호를 newPw 저장
		
		if(memberVo != null) {
		
		//회원정보를 메일센더에 보내서 임시비번으로 바꾸고 메일 전송
		memberService.sendMail(memberVo);
			model.addAttribute("MemberVo", memberVo);
			return "member/bizCheckedPwPage";
		}else {
			return "member/bizCheckPwFailPage";
		}
		
	}
	
	@RequestMapping("bizCheckPwFailPage")
	public String bizCheckPwFailPage() {
		
		return "member/bizCheckPwFailPage";
	}
	
	@RequestMapping("unlinkKakao")
	public String unlinkKakao(Model model) {
		
		model.addAttribute("jsKey", memberService.getJSKey());
		
		return "member/unlinkKakao";
	}

}
