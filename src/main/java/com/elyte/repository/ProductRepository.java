package com.elyte.repository;
import com.elyte.domain.Product;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;


public interface ProductRepository extends CrudRepository<Product, UUID> {
}
