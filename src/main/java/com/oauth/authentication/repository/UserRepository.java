package com.oauth.authentication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oauth.authentication.model.UserDtls;


public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	public UserDtls findByEmail(String email);

	public List<UserDtls> findByRole(String role);

	public UserDtls findByResetToken(String token);

	public Boolean existsByEmail(String email);
	
	public Optional<UserDtls> findById(Integer id);
	
}
