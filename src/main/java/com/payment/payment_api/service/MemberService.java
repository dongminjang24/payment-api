package com.payment.payment_api.service;


import org.springframework.stereotype.Service;

import com.payment.payment_api.exception.CustomException;
import com.payment.payment_api.exception.ErrorCode;
import com.payment.payment_api.model.entity.Member;
import com.payment.payment_api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public void saveMember(Member member) {
		memberRepository.save(member);
	}

	public Member updateMember(String email) {
		Member member = verifyMember(email);
		saveMember(member);
		return member;
	}

	public Member verifyMember(String email) {
		return memberRepository.findByEmail(email).orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
