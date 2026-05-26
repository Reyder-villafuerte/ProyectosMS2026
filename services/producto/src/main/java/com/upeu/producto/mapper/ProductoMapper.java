package com.upeu.producto.mapper;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.entity.Producto;

public final class ProductoMapper {
	private ProductoMapper() {
	}

	public static Producto toEntity(ProductoRequest request) {
		return Producto.builder()
				.nombre(request.getNombre())
				.descripcion(request.getDescripcion())
				.idCategoria(request.getIdCategoria())
				.precio(request.getPrecio())
				.stock(request.getStock())
				.build();
	}

	public static ProductoResponse toResponse(Producto entity) {
		return ProductoResponse.builder()
				.id(entity.getId())
				.nombre(entity.getNombre())
				.descripcion(entity.getDescripcion())
				.idCategoria(entity.getIdCategoria())
				.precio(entity.getPrecio())
				.stock(entity.getStock())
				.build();
	}
}

