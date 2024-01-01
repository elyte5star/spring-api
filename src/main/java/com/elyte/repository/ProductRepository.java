package com.elyte.repository;

import com.elyte.domain.Product;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

//better to extend JpaRepository to have both PagingAndSortingRepository and CrudRepository
public interface ProductRepository
        extends PagingAndSortingRepository<Product, String>, CrudRepository<Product, String> {

    boolean existsByName(String name);

    List<Product> findAllByPrice(double price, Pageable pageable);

}
