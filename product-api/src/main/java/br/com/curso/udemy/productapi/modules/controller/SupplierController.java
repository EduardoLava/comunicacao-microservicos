package br.com.curso.udemy.productapi.modules.controller;

import br.com.curso.udemy.productapi.modules.dto.SupplierRequest;
import br.com.curso.udemy.productapi.modules.dto.SupplierResponse;
import br.com.curso.udemy.productapi.modules.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public SupplierResponse save(@RequestBody SupplierRequest supplierRequest){
        return supplierService.update(supplierRequest);
    }

    @PutMapping("{id}")
    public SupplierResponse update(@RequestBody SupplierRequest supplierRequest, @PathVariable Integer id){
        return supplierService.update(supplierRequest, id);
    }

    @GetMapping
    public List<SupplierResponse> findAll(){
        return supplierService.findByAll();
    }

    @GetMapping("{id}")
    public SupplierResponse findId(@PathVariable Integer id){
        return supplierService.findIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<SupplierResponse> findByName(@PathVariable String name){
        return supplierService.findByName(name);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Integer id){
        supplierService.delete(id);
    }
}
