package br.com.curso.udemy.productapi.modules.sales.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesProductResponseDTO {

    private List<String> salesIds;
}
