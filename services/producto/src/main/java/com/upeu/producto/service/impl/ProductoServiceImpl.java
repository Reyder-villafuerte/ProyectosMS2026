package com.upeu.producto.service.impl;

import com.upeu.producto.client.CatalogoClient;
import com.upeu.producto.dto.CategoriaDto;
import com.upeu.producto.dto.ProductoRequest;
import com.upeu.producto.dto.ProductoResponse;
import com.upeu.producto.mapper.ProductoMapper;
import com.upeu.producto.repository.ProductoRepository;
import com.upeu.producto.service.ProductoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

	private final ProductoRepository productoRepository;
	private final CatalogoClient catalogoClient;

	@Override
	public List<ProductoResponse> findAll() {
		return productoRepository.findAll().stream()
				.map(producto -> toResponseConCategoria(producto, false))
				.toList();
	}

	@Override
	public ProductoResponse create(ProductoRequest request) {
		validarCategoriaExiste(request.getIdCategoria());
		var entity = ProductoMapper.toEntity(request);
		var saved = productoRepository.save(entity);
		return toResponseConCategoria(saved, true);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductoResponse findById(Long id) {
		var producto = productoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
		return toResponseConCategoria(producto, true);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductoResponse findDetalleById(Long id) {
		var producto = productoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));

		return toResponseConCategoria(producto, true);
	}

	@Override
	@Transactional
	public ProductoResponse update(Long id, ProductoRequest request) {
		var producto = productoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
		validarCategoriaExiste(request.getIdCategoria());

		producto.setNombre(request.getNombre());
		producto.setDescripcion(request.getDescripcion());
		producto.setIdCategoria(request.getIdCategoria());
		producto.setPrecio(request.getPrecio());
		producto.setStock(request.getStock());

		var saved = productoRepository.save(producto);
		return toResponseConCategoria(saved, true);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!productoRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id);
		}
		productoRepository.deleteById(id);
	}

	@Override
	@Transactional
	public ProductoResponse descontarStock(Long id, Integer cantidad) {
		var producto = productoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));

		if (producto.getStock() < cantidad) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para producto: " + id);
		}

		producto.setStock(producto.getStock() - cantidad);
		var saved = productoRepository.save(producto);
		return toResponseConCategoria(saved, false);
	}

	private void validarCategoriaExiste(Long idCategoria) {
		try {
			catalogoClient.findCategoriaById(idCategoria);
		} catch (Exception ex) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Categoria no valida o no disponible para id: " + idCategoria
			);
		}
	}

	private ProductoResponse toResponseConCategoria(com.upeu.producto.entity.Producto producto, boolean categoriaRequerida) {
		CategoriaDto categoria = null;
		try {
			categoria = catalogoClient.findCategoriaById(producto.getIdCategoria());
		} catch (Exception ex) {
			if (categoriaRequerida) {
				throw new ResponseStatusException(
						HttpStatus.SERVICE_UNAVAILABLE,
						"No se pudo obtener la categoria del catalogo para idCategoria: " + producto.getIdCategoria()
				);
			}
		}

		return ProductoResponse.builder()
				.id(producto.getId())
				.nombre(producto.getNombre())
				.descripcion(producto.getDescripcion())
				.idCategoria(producto.getIdCategoria())
				.precio(producto.getPrecio())
				.stock(producto.getStock())
				.categoria(categoria)
				.build();
	}
}

