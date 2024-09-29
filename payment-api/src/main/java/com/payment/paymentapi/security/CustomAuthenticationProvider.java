package com.payment.paymentapi.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.payment.security.CustomAuthenticationToken;
import com.payment.model.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final CustomUserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String loginId = authentication.getName();
		String password = (String) authentication.getCredentials();

		Member entity = (Member) userDetailsService.loadUserByUsername(loginId);

		if(!passwordEncoder.matches(password, entity.getPassword())) {
			throw new BadCredentialsException("Invalid Password");
		}

		return new CustomAuthenticationToken(entity, null, entity.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(CustomAuthenticationToken.class);
	}
}