package com.upeu.user.web.dto;

import java.time.OffsetDateTime;

public record UserResponse(
		Long id,
		String username,
		String email,
		String fullName,
		OffsetDateTime createdAt
) {
}

