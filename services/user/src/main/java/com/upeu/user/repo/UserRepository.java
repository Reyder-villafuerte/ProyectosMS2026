package com.upeu.user.repo;

import com.upeu.user.domain.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}

