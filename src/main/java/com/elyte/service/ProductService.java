package com.elyte.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.elyte.controllers.ProductsController;
import com.elyte.domain.Product;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.ProductRepository;
import java.util.Optional;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(ProductsController.class);

    public ResponseEntity<Iterable<Product>> getAllProducts() {
        Iterable<Product> allProducts = productRepository.findAll();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }


    public ResponseEntity<?> createOneProduct(Product product) {
        try {
            productRepository.save(product);
            HttpHeaders responseHeaders = new HttpHeaders();
            URI newUserUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pid}").buildAndExpand(product.getPid()).toUri();
            responseHeaders.setLocation(newUserUri);
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    public ResponseEntity<Product> ProductById(String pid) throws ResourceNotFoundException {
        Optional<Product> product = productRepository.findById(pid);

        if (!product.isPresent()) {

            throw new ResourceNotFoundException("Product with id :" + pid + " not found!");
        }
        return new ResponseEntity<>(product.get(), HttpStatus.OK);

    }

    public ResponseEntity<HttpStatus> deleteProduct(String pid) throws ResourceNotFoundException {
        Optional<Product> product = productRepository.findById(pid);

        if (product.isPresent()) {
            try {
                productRepository.deleteById(pid);
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                log.error(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new ResourceNotFoundException("Product with id :" + pid + " not found!");

    }

    public ResponseEntity<Iterable<String>> createMany(List<Product> products) {

        if(!products.isEmpty()){
        
            Iterable<Product> productsSaved= productRepository.saveAll(products);
            List<String> productsPids = new ArrayList<>();
            for (Product product : productsSaved) {
                productsPids.add(product.getPid());
            }
            return new ResponseEntity<>(productsPids, HttpStatus.OK);
        }
       
        throw new NullPointerException("EMPTY LIST OF INPUTS");
    }

    public ResponseEntity<HttpStatus> updateProduct(Product product,String pid) throws ResourceNotFoundException{
        Optional<Product> productData = productRepository.findById(pid);
        if (!productData.isPresent()) {
            throw new ResourceNotFoundException("Product with id :" + pid + " not found!");
        }
        productRepository.save(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
