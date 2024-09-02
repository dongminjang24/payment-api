package com.payment.paymentapi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.payment.common.exception.CustomException;
import com.payment.common.exception.UserErrorCode;
import com.payment.model.entity.Member;
import com.payment.common.enum_type.Role;
import com.payment.paymentapi.dto.SignUpDto;
import com.payment.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void saveMember(Member member) {
		memberRepository.save(member);
	}

	public Member signUpMember(SignUpDto signUpDto) {
		memberRepository.findByEmail(signUpDto.getEmail()).ifPresent(member -> {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		});
		Member member = Member.builder()
			.name(signUpDto.getName())
			.email(signUpDto.getEmail())
			.password(passwordEncoder.encode(signUpDto.getPassword()))  // 비밀번호 암호화
			.phoneNumber(signUpDto.getPhoneNumber())
			.role(Role.ROLE_USER)
			.build();
		return memberRepository.save(member);
	}

	public Member getMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new CustomException(UserErrorCode.MEMBER_NOT_FOUND));
	}
}
