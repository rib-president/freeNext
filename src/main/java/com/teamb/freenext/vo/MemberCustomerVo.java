package com.teamb.freenext.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class MemberCustomerVo {
	private int customer_no;
	private int member_no;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date customer_birth;
	private String customer_kakao;	

	public MemberCustomerVo() {
		super();
	}

	public MemberCustomerVo(int customer_no, int member_no, Date customer_birth, String customer_kakao) {
		super();
		this.customer_no = customer_no;
		this.member_no = member_no;
		this.customer_birth = customer_birth;
		this.customer_kakao = customer_kakao;
	}

	public int getCustomer_no() {
		return customer_no;
	}

	public void setCustomer_no(int customer_no) {
		this.customer_no = customer_no;
	}

	public int getMember_no() {
		return member_no;
	}

	public void setMember_no(int member_no) {
		this.member_no = member_no;
	}

	public Date getCustomer_birth() {
		return customer_birth;
	}

	public void setCustomer_birth(Date customer_birth) {
		this.customer_birth = customer_birth;
	}

	public String getCustomer_kakao() {
		return customer_kakao;
	}

	public void setCustomer_kakao(String customer_kakao) {
		this.customer_kakao = customer_kakao;
	}
}
