package com.upeu.producto.service;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import java.util.List;

public interface ProductoService {
	List<ProductoResponse> findAll();

	ProductoResponse create(ProductoRequest request);

	ProductoResponse findById(Long id);

	ProductoResponse findDetalleById(Long id);

	ProductoResponse update(Long id, ProductoRequest request);

	void delete(Long id);

	ProductoResponse descontarStock(Long id, Integer cantidad);
}

