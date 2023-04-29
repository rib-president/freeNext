package com.teamb.freenext.mail.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.teamb.freenext.commons.MailSenderThread;
import com.teamb.freenext.mail.mapper.MailMapper;
import com.teamb.freenext.normal.mapper.NormalMapper;
import com.teamb.freenext.vo.JobCategoryVo;
import com.teamb.freenext.vo.LocalCategoryVo;
import com.teamb.freenext.vo.MemberVo;
import com.teamb.freenext.vo.ProjectBoardVo;

@Service
public class MailService {

	@Autowired
	private MailMapper mailMapper;
	
	@Autowired
	private NormalMapper normalMapper;
	
	@Autowired
	private JavaMailSender javaMailSender;

	private MailSenderThread mailSenderThread;
	private ArrayList<ProjectBoardVo> adList;
	
	@Scheduled(cron = "0 30 18 * * *")
	public void sendMail() {
		setAdList();
		
		for(MemberVo customer : getCustomer()) {
			int member_no = customer.getMember_no();
			
			shuffleAdList();
			ArrayList<ProjectBoardVo> sendList = getSendProject(member_no);
			
			if(sendList != null) {
				String desiredCategory = addDesiredCategory(member_no);
				System.out.println(customer.getMember_name() + "차례 이메일 : " + customer.getMember_email());
				String mailText = "<h1>" + customer.getMember_name() + "님을 위한 오늘의 추천 프로젝트💌</h1><br>" +
									"<h2>" + desiredCategory + " 프로젝트 정보만 가져왔어요</h2><br><br>";
				for(ProjectBoardVo send : sendList) {
					/*mailText += "<a href='http://localhost:8181/normal/projectDetailPage?project_no=" + send.getProject_no() + 
							"'><h3>" + send.getProject_name() + "</h3></a><br>";*/
					mailText += "<a href='http://home.s001lec.com:7781/normal/projectDetailPage?project_no=" + send.getProject_no() + 
							"'><h3>" + send.getProject_name() + "</h3></a><br>";
				}				
				System.out.println(mailText);
				mailSenderThread = new MailSenderThread(javaMailSender, customer.getMember_email(), "[FreeNext] 오늘의 추천 프로젝트", mailText);
				mailSenderThread.start();
				
			}
		}
	}
	
	public ArrayList<MemberVo> getCustomer() {
		return mailMapper.selectAllCustomer();
	}
	
	public ArrayList<ProjectBoardVo> getMemberAlarm(int member_no) {
		return mailMapper.selectRandomAlarm(member_no);
	}
	
	public void setAdList() {
		this.adList = mailMapper.selectAdListAll();
	}
	
	public void shuffleAdList() {
		Collections.shuffle(adList);
	}
	
	public ArrayList<ProjectBoardVo> getSendProject(int member_no) {
		ArrayList<ProjectBoardVo> sendList = getMemberAlarm(member_no);
		return addAdToSendProject(sendList);				
	}
	
	public ArrayList<ProjectBoardVo> addAdToSendProject(ArrayList<ProjectBoardVo> sendList) {
		sendList.add(adList.get(0));
		return sendList;
	}
	
	public ArrayList<JobCategoryVo> getDesiredJobList(int member_no) {
		return normalMapper.selectDesiredJob(member_no);
	}
	
	public String getJobNames(int member_no) {
		String jobNames = "";
		
		for(JobCategoryVo jobVo : getDesiredJobList(member_no)) {
			jobNames += jobVo.getJob_name() + "/";
		}
		return jobNames;
	}
	
	public ArrayList<LocalCategoryVo> getDesiredLocalList(int member_no) {
		return normalMapper.selectDesiredLocal(member_no);
	}
	
	public String getLocalNames(int member_no) {
		String localNames = "";
		
		for(LocalCategoryVo localVo : getDesiredLocalList(member_no)) {
			localNames += localVo.getLocal_name() + "/";			
		}
		return localNames;
	}
	
	public String addDesiredCategory(int member_no) {
		String desiredCategory = "";
		
		desiredCategory += getJobNames(member_no);	
		desiredCategory += getLocalNames(member_no);				
		
		return desiredCategory;
	}
}
