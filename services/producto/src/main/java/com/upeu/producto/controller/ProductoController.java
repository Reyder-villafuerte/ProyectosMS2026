package com.upeu.producto.controller;

import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.dto.StockAjusteRequest;
import com.upeu.producto.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
public class ProductoController {

	private final ProductoService productoService;

	@GetMapping
	public List<ProductoResponse> findAll() {
		return productoService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(productoService.findById(id));
	}

	@GetMapping("/detalle/{id}")
	public ResponseEntity<ProductoResponse> findDetalleById(@PathVariable Long id) {
		return ResponseEntity.ok(productoService.findDetalleById(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse create(@Valid @RequestBody ProductoRequest request) {
		return productoService.create(request);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoResponse> update(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
		return ResponseEntity.ok(productoService.update(id, request));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		productoService.delete(id);
	}

	@PatchMapping("/{id}/stock/descontar")
	public ResponseEntity<ProductoResponse> descontarStock(@PathVariable Long id,
			@Valid @RequestBody StockAjusteRequest request) {
		return ResponseEntity.ok(productoService.descontarStock(id, request.getCantidad()));
	}
}

