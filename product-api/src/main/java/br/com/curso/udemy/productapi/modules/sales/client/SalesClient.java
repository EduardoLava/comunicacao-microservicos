package br.com.curso.udemy.productapi.modules.sales.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
    name = "salesClient",
    contextId = "salesClient",
    url = "${app-config.services.sales}"
)
public interface SalesClient {

    @GetMapping("/api/order/product/{productId}")
    Optional<SalesProductResponseDTO> findSalesByProductId(@PathVariable() Integer productId);
}
