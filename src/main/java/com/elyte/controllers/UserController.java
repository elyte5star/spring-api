package com.elyte.controllers;

import com.elyte.domain.request.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.request.ModifyEntityRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    
    @Autowired
    private UserService userService;


    @GetMapping("/{userid}")
    @Operation(summary = "Get a user by userid",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findUserById(@PathVariable @Valid String userid) throws ResourceNotFoundException {
        return userService.userById(userid);
    }

    @PutMapping("/{userid}")
    @Operation(summary = "Update a user",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> updateUser(@RequestBody ModifyEntityRequest user,@PathVariable String userid) throws ResourceNotFoundException{
        return userService.updateUserInfo(user, userid);
    }

    @PostMapping("/signup")
    @Operation(summary = "Create a user")
    public ResponseEntity<CustomResponseStatus> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        return userService.addUser(createUserRequest);
    }

    @DeleteMapping("/{userid}")
    @Operation(summary = "Delete a user",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> deleteUser(@PathVariable @Valid String userid) throws ResourceNotFoundException{
        return userService.deleteUser(userid);
       
    }

}
