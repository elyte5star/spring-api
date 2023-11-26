package com.elyte.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elyte.domain.request.CreateReviewRequest;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.ResourceNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.elyte.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
public class ReviewController {


    @Autowired
    private ReviewService reviewService;


    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all reviews",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{rid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a review by rid",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findReviewById(@PathVariable String rid) throws ResourceNotFoundException {
        return reviewService.ReviewById(rid);

    }

    @PostMapping("/create-review")
    @Operation(summary = "Create a product Review")
    public ResponseEntity<CustomResponseStatus> createProductReview(@RequestBody @Valid CreateReviewRequest review ) {
        return reviewService.createReview(review);

    }

    
}
