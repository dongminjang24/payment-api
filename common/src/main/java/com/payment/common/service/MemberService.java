package com.payment.common.service;

import com.payment.common.model.dto.SignUpDto;
import com.payment.common.model.entity.Member;


public interface MemberService {
	Member signUpMember(SignUpDto signUpDto);
	Member getMemberById(Long id);
	void saveMember(Member member);
}
