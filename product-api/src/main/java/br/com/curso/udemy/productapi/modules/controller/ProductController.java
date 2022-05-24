package br.com.curso.udemy.productapi.modules.controller;

import br.com.curso.udemy.productapi.modules.dto.ProductRequest;
import br.com.curso.udemy.productapi.modules.dto.ProductResponse;
import br.com.curso.udemy.productapi.modules.dto.ProductSalesResopnse;
import br.com.curso.udemy.productapi.modules.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest productRequest) {
        return productService.save(productRequest);
    }

    @PutMapping("{id}")
    public ProductResponse update(@RequestBody ProductRequest productRequest, @PathVariable Integer id) {
        return productService.update(productRequest, id);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findByAll();
    }

    @GetMapping("{id}")
    public ProductResponse findId(@PathVariable Integer id) {
        return productService.findIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<ProductResponse> findByName(@PathVariable String name) {
        return productService.findByName(name);
    }

    @GetMapping("category/{categoryId}")
    public List<ProductResponse> findByCategoryId(@PathVariable Integer categoryId) {
        return productService.findByCategoryId(categoryId);
    }

    @GetMapping("supplier/{supplierId}")
    public List<ProductResponse> findBySupplierId(@PathVariable Integer supplierId) {
        return productService.findBySupplierId(supplierId);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Integer id) {
        productService.delete(id);
    }

    @GetMapping("{id}/sales")
    public ProductSalesResopnse findProductSales(@PathVariable Integer id) {
        return productService.findProductSales(id);
    }
}
