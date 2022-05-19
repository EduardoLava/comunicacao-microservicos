package br.com.curso.udemy.productapi.modules.service;

import br.com.curso.udemy.productapi.config.exception.ValidationException;
import br.com.curso.udemy.productapi.modules.dto.SupplierRequest;
import br.com.curso.udemy.productapi.modules.dto.SupplierResponse;
import br.com.curso.udemy.productapi.modules.model.Supplier;
import br.com.curso.udemy.productapi.modules.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductService productService;

    public SupplierResponse update(SupplierRequest request){
        validateSupplierNameInformed(request);
        var supplier = supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id){
        validateSupplierNameInformed(request);
        var supplier = Supplier.of(request);
        supplier.setId(id);
        supplier = supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public List<SupplierResponse> findByName(String name){
        if(ObjectUtils.isEmpty(name)){
            throw new ValidationException("The supplier description must be informed");
        }
        return supplierRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByAll(){
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findIdResponse(Integer id) {
        validateInformedId(id);
        return SupplierResponse.of(findById(id));
    }

    public Supplier findById(Integer id){
        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supplier for the given ID"));
    }

    private void validateSupplierNameInformed(SupplierRequest categoryRequest){
        if(ObjectUtils.isEmpty(categoryRequest.getName())){
            throw new ValidationException("The category description was not informed");
        }
    }

    private void validateInformedId(Integer id) {
        if(ObjectUtils.isEmpty(id)){
            throw new ValidationException("The supplier id must be informed");
        }
    }

    public void delete(Integer id){
        validateInformedId(id);
        if(productService.existsByCategoryId(id)){
            throw new ValidationException("You cannot delete this supplier because it's already defined by a product");
        }
        supplierRepository.deleteById(id);
    }
}
