package com.upeu.producto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StockAjusteRequest {
	@NotNull
	@Min(1)
	Integer cantidad;
}
