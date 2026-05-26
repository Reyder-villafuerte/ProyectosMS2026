package com.upeu.user.service;

import com.upeu.user.domain.UserEntity;
import com.upeu.user.repo.UserRepository;
import com.upeu.user.web.dto.UserCreateRequest;
import com.upeu.user.web.dto.UserResponse;
import com.upeu.user.web.dto.UserUpdateRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<UserResponse> list() {
		return userRepository.findAll().stream().map(UserService::toResponse).toList();
	}

	public UserResponse get(Long id) {
		return toResponse(userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
	}

	@Transactional
	public UserResponse create(UserCreateRequest req) {
		if (userRepository.existsByUsername(req.username())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
		}
		if (userRepository.existsByEmail(req.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
		}

		UserEntity user = new UserEntity();
		user.setUsername(req.username());
		user.setEmail(req.email());
		user.setFullName(req.fullName());

		return toResponse(userRepository.save(user));
	}

	@Transactional
	public UserResponse update(Long id, UserUpdateRequest req) {
		UserEntity user = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		if (!user.getEmail().equalsIgnoreCase(req.email()) && userRepository.existsByEmail(req.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
		}

		user.setEmail(req.email());
		user.setFullName(req.fullName());
		return toResponse(userRepository.save(user));
	}

	@Transactional
	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		userRepository.deleteById(id);
	}

	private static UserResponse toResponse(UserEntity user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getFullName(),
				user.getCreatedAt()
		);
	}
}

