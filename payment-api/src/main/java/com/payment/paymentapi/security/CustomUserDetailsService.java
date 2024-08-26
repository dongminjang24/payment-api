package com.payment.paymentapi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.payment.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("Attempting to load user with email: {}", email);

		log.info("loadUserByUsername실행");
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> {
				log.error("User not found with email: {}", email);
				return new UsernameNotFoundException("유저를 찾을 수 없습니다. 이메일: " + email);
			});	}
}