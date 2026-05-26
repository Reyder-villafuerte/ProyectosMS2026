package com.upeu.user.web;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserInstanceController {

	private final Environment environment;

	@GetMapping("/instancia")
	public Map<String, String> instancia() {
		return Map.of(
				"app", "user",
				"port", environment.getProperty("local.server.port", "N/A"),
				"host", environment.getProperty("HOSTNAME", "desconocido")
		);
	}
}

