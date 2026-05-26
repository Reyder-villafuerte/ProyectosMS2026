package com.upeu.pedido.client;

import com.upeu.pedido.dto.ProductoDto;
import com.upeu.pedido.dto.StockAjusteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "producto")
public interface ProductoClient {
	@GetMapping("/api/v1/productos/{id}")
	ProductoDto findById(@PathVariable("id") Long id);

	@PatchMapping("/api/v1/productos/{id}/stock/descontar")
	ProductoDto descontarStock(@PathVariable("id") Long id, @RequestBody StockAjusteRequest request);
}
