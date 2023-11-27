package com.elyte.service;

import com.elyte.domain.Product;
import com.elyte.domain.Review;
import com.elyte.domain.request.CreateReviewRequest;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.elyte.repository.ProductRepository;
import com.elyte.repository.ReviewRepository;
import java.util.Optional;
import com.elyte.utils.ApplicationConsts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

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
            CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.CREATED.value(),
                    ApplicationConsts.I200_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), newReview.getRid());
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        }
        throw new ResourceNotFoundException("Product with id :" + review.getPid() + " not found!");

    }

    public ResponseEntity<CustomResponseStatus> getAllReviews() {
        Iterable<Review> allProductsReviews = reviewRepository.findAll();
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), allProductsReviews);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> ReviewById(String rid) throws ResourceNotFoundException {
        Optional<Review> review = reviewRepository.findById(rid);

        if (review.isPresent()) {
            CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), review.get());
            return new ResponseEntity<>(resp, HttpStatus.OK);

        }
        throw new ResourceNotFoundException("Product review with id :" + rid + " not found!");

    }
}
