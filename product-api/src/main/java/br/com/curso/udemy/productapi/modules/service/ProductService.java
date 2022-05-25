package br.com.curso.udemy.productapi.modules.service;

import br.com.curso.udemy.productapi.config.exception.ValidationException;
import br.com.curso.udemy.productapi.modules.dto.*;
import br.com.curso.udemy.productapi.modules.model.Product;
import br.com.curso.udemy.productapi.modules.repository.ProductRepository;
import br.com.curso.udemy.productapi.modules.sales.client.SalesClient;
import br.com.curso.udemy.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.curso.udemy.productapi.modules.sales.enums.SalesStatus;
import br.com.curso.udemy.productapi.modules.sales.rabbit.SalesConfirmationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
    @Autowired
    private SalesConfirmationSender sender;
    @Autowired
    private SalesClient salesClient;

    public ProductResponse save(ProductRequest request) {
        validateProductDataNotInformed(request);
        validateProductCategoryAndSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product = productRepository.save(product);
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id) {
        validateProductDataNotInformed(request);
        validateProductCategoryAndSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product.setId(id);
        product = productRepository.save(product);
        return ProductResponse.of(product);
    }

    private void validateProductDataNotInformed(ProductRequest productRequest) {
        if (ObjectUtils.isEmpty(productRequest.getName())) {
            throw new ValidationException("The product's name was not informed");
        }
        if (ObjectUtils.isEmpty(productRequest.getQuantityAvailable())) {
            throw new ValidationException("The product's quantity was not informed");
        }
        if (productRequest.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity should not be less or equal to zero");
        }
    }

    private void validateProductCategoryAndSupplierIdInformed(ProductRequest productRequest) {
        if (ObjectUtils.isEmpty(productRequest.getCategoryId())) {
            throw new ValidationException("The category ID was not informed");
        }
        if (ObjectUtils.isEmpty(productRequest.getSupplierId())) {
            throw new ValidationException("The supplier ID was not informed");
        }
    }

    public List<ProductResponse> findByName(String name) {
        if (ObjectUtils.isEmpty(name)) {
            throw new ValidationException("The product name must be informed");
        }
        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByAll() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findIdResponse(Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            throw new ValidationException("The product id must be informed");
        }
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no product for the given ID"));
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if (ObjectUtils.isEmpty(supplierId)) {
            throw new ValidationException("The supplier id must be informed");
        }
        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if (ObjectUtils.isEmpty(categoryId)) {
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

    public void delete(Integer id) {
        productRepository.deleteById(id);
    }


    public void updateProductStock(ProductStockDTO productStockDTO) {
        try {
            validateStockUpdateData(productStockDTO);
            updateStock(productStockDTO);
        } catch (Exception exception) {
            log.error("Error whiling trying to update stock for message error: " + exception.getMessage(), exception);
            sender.sendSalesConfirmationMessage(new SalesConfirmationDTO(
                    productStockDTO.getSalesId(),
                    SalesStatus.REJECTED
            ));
        }
    }

    @Transactional
    private void updateStock(ProductStockDTO productStockDTO) {

        var productsForUpdate = new ArrayList<Product>();

        productStockDTO.getProducts().forEach(salesProduct -> {
            var existingProduct = findById(salesProduct.getProductId());
            validateQuantityInStock(salesProduct, existingProduct);
            existingProduct.updateStock(salesProduct.getQuantity());
            productsForUpdate.add(existingProduct);
        });
        if (!ObjectUtils.isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(productStockDTO.getSalesId(), SalesStatus.APPROVED);
            sender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantityInStock(
            ProductQuantityDTO salesProduct,
            Product product) {
        if (salesProduct.getQuantity() > product.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("The product %s is out of stock", product.getId()));
        }
    }

    private void validateStockUpdateData(ProductStockDTO product) {
        if (ObjectUtils.isEmpty(product)
                || ObjectUtils.isEmpty(product.getSalesId())) {
            throw new ValidationException("The product data or sales ID cannot be null");
        }
        if (ObjectUtils.isEmpty(product.getProducts())) {
            throw new ValidationException("The sales products must be informed");
        }
        product.getProducts()
                .forEach(salesProduct -> {
                    if (ObjectUtils.isEmpty(salesProduct.getProductId())
                            || ObjectUtils.isEmpty(salesProduct.getQuantity())) {
                        throw new ValidationException("The product ID and the quantity must be informed");
                    }
                });
    }

    public ProductSalesResopnse findProductSales(Integer id) {

        var product = findById(id);
        try {
            var sales = salesClient.findSalesByProductId(id)
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product"));
            return ProductSalesResopnse.of(product, sales.getSalesIds());
        } catch (Exception e) {
            throw new ValidationException("There was an error trying to get the product's sales."+ e.getMessage());
        }
    }

    public void checkProductsStock(ProductCheckStockRequestDTO request){
        if(ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getProducts())){
            throw new ValidationException("The request data must be informed");
        }
        request.getProducts().forEach(this::validateStock);
    }

    private void validateStock(ProductQuantityDTO productQuantity){
        if(ObjectUtils.isEmpty(productQuantity.getProductId()) || ObjectUtils.isEmpty(productQuantity.getQuantity())){
            throw new ValidationException("Product ID and quantity must be informed");
        }
        var product = findById(productQuantity.getProductId());
        if(product.getQuantityAvailable() < productQuantity.getQuantity()){
            throw new ValidationException(String.format("The product %s is out of stock", product.getId()));
        }
    }
}
