
package com.elyte.controllers;

import com.elyte.domain.request.CreateReviewRequest;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.ProductService;
import com.elyte.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.elyte.domain.response.CustomResponseStatus;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    @Operation(summary = "Get all products")
    public ResponseEntity<CustomResponseStatus> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);

    }

    @GetMapping("/price")
    @Operation(summary = "Get products by specific price")
    public ResponseEntity<CustomResponseStatus> findProductsByPrice(@RequestParam @Valid double price,
            @RequestParam(value = "page", required = false) int pageNumber,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            Pageable pageable) {
        return productService.findProductsByPrice(price, pageable);

    }

    @PostMapping("/create/review")
    @Operation(summary = "Create a product review")
    public ResponseEntity<CustomResponseStatus> createProductReview(@RequestBody @Valid CreateReviewRequest review) {
        return reviewService.createReview(review);

    }

    @GetMapping("/{pid}/reviews")
    @Operation(summary = "Get reviews of a product by pid")
    public ResponseEntity<CustomResponseStatus> getAllReviewsByProductId(@PathVariable @Valid String pid)
            throws ResourceNotFoundException {
        return reviewService.ReviewsByProductId(pid);

    }

    @GetMapping("/{pid}")
    @Operation(summary = "Get a product by pid")
    public ResponseEntity<CustomResponseStatus> findProductById(@PathVariable @Valid String pid)
            throws ResourceNotFoundException {
        return productService.ProductById(pid);

    }

}