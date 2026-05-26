package com.upeu.producto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductoRequest {
	@NotBlank
	String nombre;

	String descripcion;

	@NotNull
	Long idCategoria;

	@NotNull
	@DecimalMin("0.00")
	BigDecimal precio;

	@NotNull
	@Min(0)
	Integer stock;
}

