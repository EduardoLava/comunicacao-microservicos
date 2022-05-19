package br.com.curso.udemy.productapi.modules.service;

import br.com.curso.udemy.productapi.config.exception.ValidationException;
import br.com.curso.udemy.productapi.modules.dto.ProductRequest;
import br.com.curso.udemy.productapi.modules.dto.ProductResponse;
import br.com.curso.udemy.productapi.modules.model.Product;
import br.com.curso.udemy.productapi.modules.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    @Lazy
    private CategoryService categoryService;
    @Autowired
    @Lazy
    private SupplierService supplierService;

    public ProductResponse save(ProductRequest request){
        validateProductDataNotInformed(request);
        validateProductCategoryAndSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product = productRepository.save(product);
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id){
        validateProductDataNotInformed(request);
        validateProductCategoryAndSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product.setId(id);
        product = productRepository.save(product);
        return ProductResponse.of(product);
    }

    private void validateProductDataNotInformed(ProductRequest  productRequest){
        if(ObjectUtils.isEmpty(productRequest.getName())){
            throw new ValidationException("The product's name was not informed");
        }
        if(ObjectUtils.isEmpty(productRequest.getQuantityAvailable())){
            throw new ValidationException("The product's quantity was not informed");
        }
        if(productRequest.getQuantityAvailable() <= ZERO){
            throw new ValidationException("The quantity should not be less or equal to zero");
        }
    }

    private void validateProductCategoryAndSupplierIdInformed(ProductRequest  productRequest){
        if(ObjectUtils.isEmpty(productRequest.getCategoryId())){
            throw new ValidationException("The category ID was not informed");
        }
        if(ObjectUtils.isEmpty(productRequest.getSupplierId())){
            throw new ValidationException("The supplier ID was not informed");
        }
    }

    public List<ProductResponse> findByName(String name){
        if(ObjectUtils.isEmpty(name)){
            throw new ValidationException("The product name must be informed");
        }
        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByAll(){
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findIdResponse(Integer id) {
        if(ObjectUtils.isEmpty(id)){
            throw new ValidationException("The product id must be informed");
        }
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id){
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no product for the given ID"));
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId){
        if(ObjectUtils.isEmpty(supplierId)){
            throw new ValidationException("The supplier id must be informed");
        }
        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId){
        if(ObjectUtils.isEmpty(categoryId)){
            throw new ValidationException("The category id must be informed");
        }
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer suppplierId) {
        return productRepository.existsBySupplierId(suppplierId);
    }

    public void delete(Integer id){
        productRepository.deleteById(id);
    }
}
