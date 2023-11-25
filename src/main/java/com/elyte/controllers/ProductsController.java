
package com.elyte.controllers;

import com.elyte.domain.Product;
import com.elyte.domain.Review;
import com.elyte.domain.request.CreateProductRequest;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import com.elyte.domain.response.CustomResponseStatus;




@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @GetMapping("")
    @Operation(summary = "Get All Products")
    public ResponseEntity<CustomResponseStatus> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> deleteProduct(@PathVariable String pid) throws ResourceNotFoundException {
        return productService.deleteProduct(pid);

    }

    @PutMapping("/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> updateProduct(@RequestBody Product product, @PathVariable String pid)
            throws ResourceNotFoundException {
        return productService.updateProduct(product, pid);
    }

    @GetMapping("/{pid}")
    @Operation(summary = "Get A Product By pid")
    public ResponseEntity<CustomResponseStatus> findProductById(@PathVariable String pid) throws ResourceNotFoundException {
        return productService.ProductById(pid);

    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> createProduct(@RequestBody @Valid CreateProductRequest product) {
        return productService.createOneProduct(product);

    }

    @PostMapping("/create/many")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create Many Products", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> createManyProducts(@RequestBody List<Product> products) {
        return productService.createMany(products);
    }

    @PostMapping("/create-review")
    @Operation(summary = "Create A Product Review")
    public ResponseEntity<CustomResponseStatus> createProductReview(@RequestBody @Valid Review review ) {
        return productService.createReview(review);

    }

}