package com.upeu.pedido.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductoDto {
	Long id;
	String nombre;
	String descripcion;
	Long idCategoria;
	BigDecimal precio;
	Integer stock;
}
