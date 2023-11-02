package com.elyte.controllers;

import com.elyte.domain.User;
import com.elyte.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;



@RestController
@RequestMapping("/users")
public class UserController {
    Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> allUsers = userRepository.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long userid) throws Exception {
        Optional<User> user = userRepository.findById(userid);
        if(!user.isPresent()){
            log.info("User not found!");
            throw new Exception("User not found!");
        }
        
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
        
    }

    @PutMapping("/{userid}")
    public ResponseEntity<?> updateUser(@RequestBody User user,@PathVariable Long userid){
        User newUser = userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        userRepository.save(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newUserUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        responseHeaders.setLocation(newUserUri);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }
    @DeleteMapping("/{userid}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userid){
        userRepository.deleteById(userid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
