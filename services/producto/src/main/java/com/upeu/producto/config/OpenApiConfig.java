package com.upeu.producto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI productoOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("producto API")
						.version("v1")
						.contact(new Contact()
								.name("Equipo producto")
								.email("producto@upeu.edu.pe")));
	}
}

