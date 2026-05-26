package com.upeu.producto.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductoResponse {
	Long id;
	String nombre;
	String descripcion;
	Long idCategoria;
	BigDecimal precio;
	Integer stock;
	CategoriaDto categoria;
}

