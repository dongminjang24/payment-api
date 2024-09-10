package com.payment.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.payment.model.entity.Member;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	@Query(value = "SELECT SLEEP(:seconds)", nativeQuery = true)
	void sleepMySQL(@Param("seconds") int seconds);
}
