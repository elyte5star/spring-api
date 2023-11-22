package com.elyte.controllers;

import com.elyte.domain.User;
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
import java.util.UUID;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import com.elyte.domain.response.GetUserResponse;
import com.elyte.domain.response.Status;
import com.elyte.domain.response.CreateUserResponse;
import com.elyte.domain.request.ModifyEntityRequest;
import com.elyte.domain.response.GetUsersResponse;
import com.elyte.domain.response.ModifyUserResponse;

@RestController
@RequestMapping("/users")
public class UserController {

    
    @Autowired
    private UserService userService;


    @GetMapping("")
    @Operation(summary = "Get All Users", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<GetUsersResponse> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userid}")
    @Operation(summary = "Get A User By USERID",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<GetUserResponse> findUserById(@PathVariable UUID userid) throws ResourceNotFoundException {
        return userService.userById(userid);
    }

    @PutMapping("/{userid}")
    @Operation(summary = "Update A User",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ModifyUserResponse> updateUser(@RequestBody ModifyEntityRequest user,@PathVariable UUID userid) throws ResourceNotFoundException{
        return userService.updateUserInfo(user, userid);
    }

    @PostMapping("/signup")
    @Operation(summary = "Create A User")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
        return userService.addUser(createUserRequest);
    }

    @DeleteMapping("/{userid}")
    @Operation(summary = "Delete A User",security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Status> deleteUser(@PathVariable UUID userid) throws ResourceNotFoundException{
        return userService.deleteUser(userid);
       
    }

}
