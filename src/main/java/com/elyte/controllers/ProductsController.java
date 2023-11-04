package com.elyte.controllers;

import com.elyte.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Optional;
import com.elyte.domain.Product;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;


@RestController
@RequestMapping("/products")
public class ProductsController {
    
    Logger log = LoggerFactory.getLogger(UserController.class);

    private ProductRepository productRepository;


    @GetMapping("/")
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        log.info("Getting products");
        Iterable<Product> allProducts = productRepository.findAll();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }


    @DeleteMapping("/{pid}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long pid){
        productRepository.deleteById(pid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{pid}")
    public ResponseEntity<?> updateProduct(@RequestBody Product product,@PathVariable Long pid){
        productRepository.save(product);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{pid}")
    public ResponseEntity<Product> findProductById(@PathVariable Long pid) throws Exception {
        Optional<Product> product = productRepository.findById(pid);
        if(!product.isPresent()){
            
            throw new Exception("Product not found!");
        }
        return new ResponseEntity<>(product.get(), HttpStatus.OK);
        
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        productRepository.save(product);
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pid}").buildAndExpand(product.getPid()).toUri();
        responseHeaders.setLocation(newUserUri);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }


    @PostMapping("/create/many")
    public ResponseEntity<Iterable<Long>>createManyProducts(@RequestBody Iterable<Product> products) {
        productRepository.saveAll(products);
        List<Long> productsPids = new ArrayList<>();
        Iterable<Product> allProducts = productRepository.findAll();
        for(Product product : allProducts) {
            productsPids.add(product.getPid());
        }
        return new ResponseEntity<>(productsPids, HttpStatus.OK);
    }
   
}
