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

import com.payment.model.entity.Member;
import com.payment.paymentapi.dto.SignUpDto;
import com.payment.paymentapi.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpDto signUpDto) {
		Member member = memberService.signUpMember(signUpDto);
		Map<String, String> response = new HashMap<>();
		response.put("message", "회원가입이 완료되었습니다.");
		response.put("userId", member.getId().toString());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	public ResponseEntity<Member> getCurrentUser(HttpSession session) {
		Long memberId = (Long) session.getAttribute("MEMBER_ID");
		if (memberId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		Member member = memberService.getMemberById(memberId);
		return ResponseEntity.ok(member);
	}
}