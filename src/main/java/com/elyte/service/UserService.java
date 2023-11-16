 package com.elyte.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.elyte.controllers.UserController;
import com.elyte.domain.User;
import com.elyte.domain.request.CreateUserRequest;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.UserRepository;
import com.elyte.utils.ApplicationConsts;
import com.elyte.domain.response.CreateUserResponse;
import com.elyte.domain.response.Status;
import com.elyte.domain.response.GetUserResponse;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    LocalDateTime current = LocalDateTime.now();

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public ResponseEntity<Iterable<User>> getUsers() {
        Iterable<User> allUsers = userRepository.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    public ResponseEntity<CreateUserResponse> addUser(CreateUserRequest createUserRequest) {

        try {

            User newUser = new User();
            newUser.setUsername(createUserRequest.getUsername());
            newUser.setPassword(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
            newUser.setTelephone(createUserRequest.getTelephone());
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setLastLoginDate("0");
            newUser.setCreatedBy(createUserRequest.getUsername());
            newUser.setAdmin(createUserRequest.isAdmin());
            newUser.setEnabled(createUserRequest.isEnabled());
            userRepository.save(newUser);
            Status status = Status.build(HttpStatus.CREATED.value(), ApplicationConsts.I201_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));

            CreateUserResponse response = CreateUserResponse.build(status, newUser.getUserid());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<GetUserResponse> userById(UUID userid) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(userid);
        if (!user.isPresent()) {

            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        Status status = Status.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));
        GetUserResponse getUserResponse = GetUserResponse.build(status, user.get());
        return new ResponseEntity<>(getUserResponse, HttpStatus.OK);

    }

    public ResponseEntity<HttpStatus> updateUserInfo(User user, UUID userid) throws ResourceNotFoundException {
        Optional<User> userData = userRepository.findById(userid);
        if (!userData.isPresent()) {
            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> deleteUser(UUID userid) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(userid);
        if (user.isPresent()) {
            try {
                userRepository.deleteById(userid);
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                log.error(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new ResourceNotFoundException("User with id :" + userid + " not found!");

    }

}
