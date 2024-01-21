package com.elyte.service;

import com.elyte.domain.Product;
import com.elyte.domain.Review;
import com.elyte.domain.request.CreateReviewRequest;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.elyte.repository.ProductRepository;
import com.elyte.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;
import com.elyte.utils.UtilityFunctions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReviewService extends UtilityFunctions {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<CustomResponseStatus> createReview(CreateReviewRequest review) {
        // boolean poductExist = productRepository.existsById(review.getPid());

        Optional<Product> product = productRepository.findById(review.getPid());

        if (product.isPresent()) {
            Review newReview = new Review();
            newReview.setComment(review.getComment());
            newReview.setEmail(review.getEmail());
            newReview.setRating(review.getRating());
            newReview.setProduct(product.get());
            reviewRepository.save(newReview);
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.CREATED.value(),
                    this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(), newReview.getRid());
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        }
        throw new ResourceNotFoundException("Product with id :" + review.getPid() + " not found!");

    }

    public ResponseEntity<CustomResponseStatus> getAllReviews() {
        Iterable<Review> allProductsReviews = reviewRepository.findAll();
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS,
                this.SRC, this.timeNow(), allProductsReviews);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> ReviewsByProductId(String pid) throws ResourceNotFoundException {
        Optional<Product> product = productRepository.findById(pid);
        if (product.isPresent()) {
            List<Review> reviews = reviewRepository.findByProductPid(pid);
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(), reviews);
            return new ResponseEntity<>(resp, HttpStatus.OK);

        }
        throw new ResourceNotFoundException("Product with id :" + pid + " not found!");

    }
}
