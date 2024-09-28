package com.payment.paymentapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.common.response.CommonResponse;
import com.payment.common.response.dto.SignUpResponseDto;
import com.payment.model.entity.Member;
import com.payment.paymentapi.dto.SignUpDto;
import com.payment.paymentapi.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/member")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public CommonResponse<?> signUp(@RequestBody SignUpDto signUpDto) {
		Member member = memberService.signUpMember(signUpDto);
		SignUpResponseDto sign = SignUpResponseDto.builder()
			.userId(member.getId())
			.build();
		return new CommonResponse<>(sign);
	}

	@GetMapping("/me")
	public ResponseEntity<CommonResponse<Member>> getCurrentUser(HttpSession session) {
		Long memberId = (Long) session.getAttribute("MEMBER_ID");
		if (memberId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonResponse<>(null));
		}
		Member member = memberService.getMemberById(memberId);
		return ResponseEntity.ok(new CommonResponse<>(member));
	}
}