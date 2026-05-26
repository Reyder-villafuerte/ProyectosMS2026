package com.upeu.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PedidoRequest {
	Long idUsuario;

	@Valid
	@NotEmpty
	List<PedidoDetalleRequest> detalles;
}
