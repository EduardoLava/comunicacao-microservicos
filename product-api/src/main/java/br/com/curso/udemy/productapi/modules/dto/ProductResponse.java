package br.com.curso.udemy.productapi.modules.dto;

import br.com.curso.udemy.productapi.modules.model.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {

    private Integer id;
    private String name;
    @JsonProperty("quantity_available")
    private Integer quantityAvailable;
    @JsonProperty("created_ad")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private SupplierResponse supplier;
    private CategoryResponse category;

    public static ProductResponse of(Product product){
        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .createdAt(product.getCreatedAt())
            .quantityAvailable(product.getQuantityAvailable())
            .supplier(SupplierResponse.of(product.getSupplier()))
            .category(CategoryResponse.of(product.getCategory()))
            .build();
    }
}
