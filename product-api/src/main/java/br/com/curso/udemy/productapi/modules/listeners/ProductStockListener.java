package br.com.curso.udemy.productapi.modules.listeners;

import br.com.curso.udemy.productapi.modules.dto.ProductStockDTO;
import br.com.curso.udemy.productapi.modules.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductStockListener {

    @Autowired
    private ProductService productService;

    @RabbitListener(queues = "${app-config.rabbit.queue.product-stock}")
    public void receiveProductStockMassage(ProductStockDTO productStockDTO) throws JsonProcessingException {
        log.info(
            "Receiving message with data: {} and transactionId: {}",
            new ObjectMapper().writeValueAsString(productStockDTO),
            productStockDTO.getTransactionid()
        );
        productService.updateProductStock(productStockDTO);
    }
}
