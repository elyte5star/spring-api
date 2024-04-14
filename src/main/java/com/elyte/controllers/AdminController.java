package com.elyte.controllers;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.elyte.domain.response.JobResponse;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.queue.RabbitMqHandler;
import com.elyte.service.ProductService;
import com.elyte.service.ReviewService;
import com.elyte.service.UserService;
import com.elyte.utils.UtilityFunctions;
import com.elyte.domain.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController extends UtilityFunctions{

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RabbitMqHandler rabbitMqHandler;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/users/find-one/{userid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a user by userid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findUserById(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        return userService.userById(userid);
    }

    @GetMapping("/users/loggedUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get active users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getLoggedUsers()
            throws ResourceNotFoundException {
        return userService.getLoggedUsers();
    }

    @GetMapping("/users/isLoggedIn/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Check if a user is active", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> isActiveUser(@PathVariable @Valid String username)
            throws ResourceNotFoundException {
        return userService.isActiveUser(username);
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
        return userService.createUser(createUserRequest, locale);
    }

    @DeleteMapping("/users/delete/{userid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> deleteUser(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        return userService.deleteUser(userid);

    }

    @GetMapping("/users/getAll")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getAllUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }
    @GetMapping("/users/signup/sendOtp")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Send registration confirmation OTP",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> sendOtp(@RequestParam("username") @RequestBody @Valid String username,
            final Locale locale)
            throws ResourceNotFoundException {
        return userService.sendRegistrationOtp(username, locale);

    }


    @GetMapping("/reviews/all-reviews")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all reviews", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/reviews/{pid}/reviews")
    @Operation(summary = "Get reviews of a product by pid", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomResponseStatus> getAllReviewsByProductId(@PathVariable @Valid String pid)
            throws ResourceNotFoundException {
        return reviewService.ReviewsByProductId(pid);

    }

    @PutMapping("/products/update/{pid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update A Product", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> updateProduct(@RequestBody Product product, @PathVariable String pid)
            throws ResourceNotFoundException {
        return productService.updateProduct(product, pid);
    }

    @PostMapping("/products/create-product")
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

    @GetMapping("/jobs/{jid}/tasks")
    @Operation(summary = "Get tasks of a job by jid", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomResponseStatus> getTasksByJobId(@PathVariable @Valid String jid)
            throws ResourceNotFoundException {
        List<Task> tasks = rabbitMqHandler.getTasksByJobId(jid);
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS, this.SRC, this.timeNow(), tasks);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }

    @DeleteMapping("/jobs/{jid}")
    @Operation(summary = "Delete a job by jid", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomResponseStatus> deleteJob(@PathVariable @Valid String jid)
            throws ResourceNotFoundException {
                return rabbitMqHandler.deleteJob(jid);
    }

    @GetMapping("/jobs/find-one/{jid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a job by jid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findJobById(@PathVariable @Valid String jid)
            throws ResourceNotFoundException {
        JobResponse jobResponse = rabbitMqHandler.getJobResponse(jid);
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS, this.SRC, this.timeNow(), jobResponse);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }

    @GetMapping("/tasks/find-one/{tid}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get a task by tid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findTaskById(@PathVariable @Valid String tid)
            throws ResourceNotFoundException {
        Task task = rabbitMqHandler.getTask(tid);
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS, this.SRC, this.timeNow(), task);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }

    @GetMapping("/jobs/{userid}/jobs")
    @Operation(summary = "Get jobs of a user by userid", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CustomResponseStatus> getJobsByuserId(@PathVariable @Valid String userid)
            throws ResourceNotFoundException {
        List<JobResponse> jobResponses = rabbitMqHandler.getJobsByUserid(userid);
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS, this.SRC, this.timeNow(), jobResponses);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }

}
