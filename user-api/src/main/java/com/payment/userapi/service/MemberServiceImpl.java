package com.payment.userapi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.payment.common.exception.CustomException;
import com.payment.common.exception.UserErrorCode;
import com.payment.common.model.dto.SignInDto;
import com.payment.common.model.dto.SignUpDto;
import com.payment.common.model.enum_type.Role;
import com.payment.common.repository.MemberRepository;
import com.payment.common.service.MemberService;

import com.payment.common.model.entity.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void saveMember(Member member) {
		memberRepository.save(member);
	}

	@Override
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
