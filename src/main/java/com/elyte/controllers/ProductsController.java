
package com.elyte.controllers;

import com.elyte.domain.Product;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping("")
    @Operation(summary = "Get All Products")
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable UUID pid) throws ResourceNotFoundException {
        return productService.deleteProduct(pid);

    }

    @PutMapping("/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<HttpStatus> updateProduct(@RequestBody Product product, @PathVariable UUID pid)
            throws ResourceNotFoundException {
        return productService.updateProduct(product, pid);
    }

    @GetMapping("/{pid}")
    @Operation(summary = "Get A Product By PID")
    public ResponseEntity<Product> findProductById(@PathVariable UUID pid) throws ResourceNotFoundException {
        return productService.ProductById(pid);

    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createProduct(@RequestBody @Valid Product product) {
        return productService.createOneProduct(product);

    }

    @PostMapping("/create/many")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create Many Products", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Iterable<UUID>> createManyProducts(@RequestBody Iterable<Product> products) {
        return productService.createMany(products);
    }

}