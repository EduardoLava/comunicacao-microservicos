package br.com.curso.udemy.productapi.modules.repository;

import br.com.curso.udemy.productapi.modules.model.Category;
import br.com.curso.udemy.productapi.modules.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameContainingIgnoreCase(String name);
}
