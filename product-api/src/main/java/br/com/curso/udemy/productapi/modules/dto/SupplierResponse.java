package br.com.curso.udemy.productapi.modules.dto;

import br.com.curso.udemy.productapi.modules.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SupplierResponse {

    private Integer id;
    private String name;

    public static SupplierResponse of(Supplier supplier){
        var supplierResponse = new SupplierResponse();
        BeanUtils.copyProperties(supplier, supplierResponse);
        return supplierResponse;
    }
}
