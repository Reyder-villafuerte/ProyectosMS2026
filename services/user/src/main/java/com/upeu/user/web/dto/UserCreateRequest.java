package com.upeu.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
		@NotBlank @Size(max = 50) String username,
		@NotBlank @Email @Size(max = 120) String email,
		@NotBlank @Size(max = 120) String fullName
) {
}

