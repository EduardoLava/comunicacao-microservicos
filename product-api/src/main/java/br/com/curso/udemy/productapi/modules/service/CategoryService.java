package br.com.curso.udemy.productapi.modules.service;

import br.com.curso.udemy.productapi.config.exception.ValidationException;
import br.com.curso.udemy.productapi.modules.dto.CategoryResponse;
import br.com.curso.udemy.productapi.modules.dto.CategoryRequest;
import br.com.curso.udemy.productapi.modules.model.Category;
import br.com.curso.udemy.productapi.modules.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    public List<CategoryResponse> findByDescription(String description){
        if(ObjectUtils.isEmpty(description)){
            throw new ValidationException("The category description must be informed");
        }
        return categoryRepository
                .findByDescriptionContainingIgnoreCase(description)
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> findByAll(){
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryResponse findIdResponse(Integer id) {
       validateInformedId(id);
        return CategoryResponse.of(findById(id));
    }

    private void validateInformedId(Integer id) {
        if(ObjectUtils.isEmpty(id)){
            throw new ValidationException("The category id must be informed");
        }
    }

    public Category findById(Integer id){
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no category for the given ID"));
    }

    public CategoryResponse save(CategoryRequest request){
        validateCategoryNameInformed(request);
        var category = categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request, Integer id){
        validateCategoryNameInformed(request);
        var category = Category.of(request);
        category.setId(id);
        category = categoryRepository.save(category);
        return CategoryResponse.of(category);
    }

    private void validateCategoryNameInformed(CategoryRequest categoryRequest){
        if(ObjectUtils.isEmpty(categoryRequest.getDescription())){
            throw new ValidationException("The category description was not informed");
        }
    }

    public void delete(Integer id){
        validateInformedId(id);
        if(productService.existsByCategoryId(id)){
            throw new ValidationException("You cannot delete this supplier because it's already defined by a product");
        }
        categoryRepository.deleteById(id);
    }
}
