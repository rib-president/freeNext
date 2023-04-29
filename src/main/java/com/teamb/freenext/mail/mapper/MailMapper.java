package com.teamb.freenext.mail.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;

import com.teamb.freenext.vo.MemberVo;
import com.teamb.freenext.vo.ProjectBoardVo;

@Mapper
public interface MailMapper {
	public ArrayList<MemberVo> selectAllCustomer();
	public ArrayList<ProjectBoardVo> selectRandomAlarm(int member_no);
	public ArrayList<ProjectBoardVo> selectAdListAll();
}
