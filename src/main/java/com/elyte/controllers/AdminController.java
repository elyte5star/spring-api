package com.elyte.controllers;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.elyte.domain.Product;
import com.elyte.domain.request.CreateProductRequest;
import com.elyte.domain.request.CreateUserRequest;
import com.elyte.domain.request.ModifyEntityRequest;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.ProductService;
import com.elyte.service.ReviewService;
import com.elyte.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/users/find-one/{userid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a user by userid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findUserById(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        return userService.userById(userid);
    }


    @PutMapping("/users/update-user/{userid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> updateUser(@RequestBody ModifyEntityRequest user,
            @PathVariable String userid) throws ResourceNotFoundException {
        return userService.updateUserInfo(user, userid);
    }

    @PostMapping("/users/create-user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> createUser(@RequestBody @Valid CreateUserRequest createUserRequest,
            final Locale locale) throws DataIntegrityViolationException, MessagingException {
        return userService.addUser(createUserRequest, locale);
    }

    @DeleteMapping("/users/delete/{userid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> deleteUser(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        return userService.deleteUser(userid);

    }

    @GetMapping("/users/get-all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all reviews", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/reviews/{rid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a review by rid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findReviewById(@PathVariable String rid)
            throws ResourceNotFoundException {
        return reviewService.ReviewById(rid);

    }

    @PutMapping("/products/update/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> updateProduct(@RequestBody Product product, @PathVariable String pid)
            throws ResourceNotFoundException {
        return productService.updateProduct(product, pid);
    }

    @PostMapping("/products/create-products")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> createProduct(@RequestBody @Valid CreateProductRequest product) {
        return productService.createOneProduct(product);

    }

    @PostMapping("/products/create/many-products")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create many products", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> createManyProducts(
            @RequestBody List<CreateProductRequest> productsRequests) {
        return productService.createMany(productsRequests);
    }

    @DeleteMapping("/products/delete/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> deleteProduct(@PathVariable String pid)
            throws ResourceNotFoundException {
        return productService.deleteProduct(pid);

    }

}
