package com.payment.paymentapi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;
import com.payment.model.entity.Member;
import com.payment.common.enum_type.Role;
import com.payment.paymentapi.config.annotation.DistributeLock;
import com.payment.paymentapi.dto.SignUpDto;
import com.payment.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void saveMember(Member member) {
		memberRepository.save(member);
	}


	@Transactional
	@DistributeLock(key = "'signUpMember'") // 전역 락, 짧은 임대 시간
	public Member signUpMember(SignUpDto signUpDto) {
		memberRepository.findByEmail(signUpDto.getEmail()).ifPresent(member -> {
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
		});
		// log.debug("Before calling sleepMySQL");
		// memberRepository.sleepMySQL(3);
		// log.debug("After calling sleepMySQL");
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
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
