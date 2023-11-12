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

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public ResponseEntity<Iterable<User>> getUsers() {
        Iterable<User> allUsers = userRepository.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    public ResponseEntity<?> addUser(CreateUserRequest createUserRequest) {

        try {
            User newUser = new User();
            newUser.setUsername(createUserRequest.getUsername());
            newUser.setPassword(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
            newUser.setTelephone(createUserRequest.getTelephone());
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setLastLoginDate("0");
            newUser.setCreatedBy(createUserRequest.getUsername());
            newUser.setAdmin(createUserRequest.isAdmin());
            newUser.setActive(createUserRequest.isActive());
            newUser.setLastModifiedBy("");
            userRepository.save(newUser);
            return new ResponseEntity<>(newUser.getUserid(),HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<User> userById(UUID userid) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(userid);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);

    }

    public ResponseEntity<HttpStatus> updateUserInfo(User user, UUID userid) throws ResourceNotFoundException {
        Optional<User> userData = userRepository.findById(userid);
        if (!userData.isPresent()) {
            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> deleteUser(UUID userid) throws ResourceNotFoundException{
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
