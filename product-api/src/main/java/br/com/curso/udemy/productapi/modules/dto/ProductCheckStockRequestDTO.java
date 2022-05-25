package br.com.curso.udemy.productapi.modules.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCheckStockRequestDTO {

    private List<ProductQuantityDTO> products;
}
