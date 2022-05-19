package br.com.curso.udemy.productapi.modules.controller;

import br.com.curso.udemy.productapi.modules.dto.CategoryRequest;
import br.com.curso.udemy.productapi.modules.dto.CategoryResponse;
import br.com.curso.udemy.productapi.modules.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest categoryRequest){
        return categoryService.save(categoryRequest);
    }

    @PutMapping("{id}")
    public CategoryResponse update(@RequestBody CategoryRequest categoryRequest, @PathVariable Integer id){
        return categoryService.update(categoryRequest, id);
    }

    @GetMapping
    public List<CategoryResponse> findAll(){
        return categoryService.findByAll();
    }

    @GetMapping("{id}")
    public CategoryResponse findId(@PathVariable Integer id){
        return categoryService.findIdResponse(id);
    }

    @GetMapping("description/{description}")
    public List<CategoryResponse> findDescription(@PathVariable String description){
        return categoryService.findByDescription(description);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Integer id){
       categoryService.delete(id);
    }
}
