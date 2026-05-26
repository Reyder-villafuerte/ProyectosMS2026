package com.upeu.pedido.service.impl;

import com.upeu.pedido.client.ProductoClient;
import com.upeu.pedido.dto.PedidoDetalleResponse;
import com.upeu.pedido.dto.PedidoRequest;
import com.upeu.pedido.dto.PedidoResponse;
import com.upeu.pedido.dto.StockAjusteRequest;
import com.upeu.pedido.entity.EstadoPedido;
import com.upeu.pedido.entity.Pedido;
import com.upeu.pedido.entity.PedidoDetalle;
import com.upeu.pedido.repository.PedidoRepository;
import com.upeu.pedido.service.PedidoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

	private final PedidoRepository pedidoRepository;
	private final ProductoClient productoClient;

	@Override
	@Transactional
	public PedidoResponse create(PedidoRequest request) {
		Pedido pedido = Pedido.builder()
				.idUsuario(request.getIdUsuario())
				.estado(EstadoPedido.PENDIENTE)
				.fechaCreacion(LocalDateTime.now())
				.total(BigDecimal.ZERO)
				.build();

		BigDecimal total = BigDecimal.ZERO;

		for (var detalleRequest : request.getDetalles()) {
			var producto = obtenerProductoConResiliencia(detalleRequest.getIdProducto());

			if (producto.getStock() == null || producto.getStock() < detalleRequest.getCantidad()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Stock insuficiente para producto: " + detalleRequest.getIdProducto());
			}

			BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalleRequest.getCantidad()));
			total = total.add(subtotal);

			PedidoDetalle detalle = PedidoDetalle.builder()
					.pedido(pedido)
					.idProducto(producto.getId())
					.nombreProducto(producto.getNombre())
					.cantidad(detalleRequest.getCantidad())
					.precioUnitario(producto.getPrecio())
					.subtotal(subtotal)
					.build();

			pedido.getDetalles().add(detalle);
		}

		pedido.setTotal(total);
		Pedido saved = pedidoRepository.save(pedido);

		for (var detalle : saved.getDetalles()) {
			descontarStockConResiliencia(detalle.getIdProducto(), detalle.getCantidad());
		}

		return toResponse(saved);
	}

	@CircuitBreaker(name = "productoService", fallbackMethod = "obtenerProductoFallback")
	public com.upeu.pedido.dto.ProductoDto obtenerProductoConResiliencia(Long idProducto) {
		return productoClient.findById(idProducto);
	}

	private com.upeu.pedido.dto.ProductoDto obtenerProductoFallback(Long idProducto, Throwable ex) {
		throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"No se pudo consultar producto " + idProducto + " por indisponibilidad temporal del servicio producto."
		);
	}

	@CircuitBreaker(name = "productoService", fallbackMethod = "descontarStockFallback")
	public void descontarStockConResiliencia(Long idProducto, Integer cantidad) {
		productoClient.descontarStock(idProducto, StockAjusteRequest.builder().cantidad(cantidad).build());
	}

	private void descontarStockFallback(Long idProducto, Integer cantidad, Throwable ex) {
		throw new ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"No se pudo descontar stock del producto " + idProducto
						+ " (cantidad " + cantidad + ") por indisponibilidad temporal del servicio producto."
		);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PedidoResponse> findAll() {
		return pedidoRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PedidoResponse findById(Long id) {
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
		return toResponse(pedido);
	}

	@Override
	@Transactional
	public PedidoResponse actualizarEstado(Long id, EstadoPedido estado) {
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
		pedido.setEstado(estado);
		Pedido saved = pedidoRepository.save(pedido);
		return toResponse(saved);
	}

	private PedidoResponse toResponse(Pedido pedido) {
		return PedidoResponse.builder()
				.id(pedido.getId())
				.idUsuario(pedido.getIdUsuario())
				.total(pedido.getTotal())
				.estado(pedido.getEstado())
				.fechaCreacion(pedido.getFechaCreacion())
				.detalles(pedido.getDetalles().stream()
						.map(detalle -> PedidoDetalleResponse.builder()
								.idProducto(detalle.getIdProducto())
								.nombreProducto(detalle.getNombreProducto())
								.cantidad(detalle.getCantidad())
								.precioUnitario(detalle.getPrecioUnitario())
								.subtotal(detalle.getSubtotal())
								.build())
						.toList())
				.build();
	}
}
