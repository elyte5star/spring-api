package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.elyte.domain.User;
import com.elyte.domain.request.CreateUserRequest;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.UserRepository;
import com.elyte.utils.ApplicationConsts;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.request.ModifyEntityRequest;
import java.util.Optional;
import com.elyte.utils.CheckNullEmptyBlank;
import org.springframework.dao.DataIntegrityViolationException;
import com.elyte.utils.CheckIfUserExist;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<CustomResponseStatus> getUsers() {
        
        Iterable<User> allUsersInDb = userRepository.findAll();
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, ApplicationConsts.timeNow(), allUsersInDb);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> addUser(CreateUserRequest createUserRequest)
            throws DataIntegrityViolationException {
        User newUser = new User();
        newUser.setUsername(createUserRequest.getUsername());
        newUser.setPassword(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
        newUser.setTelephone(createUserRequest.getTelephone());
        newUser.setEmail(createUserRequest.getEmail());
        newUser.setLastLoginDate("0");
        newUser.setEnabled(createUserRequest.isEnabled());

        if (!CheckIfUserExist.isExisting(newUser, userRepository)) {
            CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.CREATED.value(),
                    ApplicationConsts.I201_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), newUser.getUserid());
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        }

        throw new DataIntegrityViolationException("A USER WITH THE DETAILS EXIST ALREADY");

    }

    public ResponseEntity<CustomResponseStatus> userById(String userid) throws ResourceNotFoundException {
       
        User user = userRepository.findByUserid(userid);
        if (user == null) {
            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC,ApplicationConsts.timeNow(), user);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> updateUserInfo(ModifyEntityRequest user, String userid)
            throws ResourceNotFoundException {
        User userInDb = userRepository.findByUserid(userid);

        if (userInDb == null) {

            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }

        if (!CheckNullEmptyBlank.check(user.getEmail()) & !(user.getEmail().equals(userInDb.getEmail()))) {
            userInDb.setEmail(user.getEmail());

        }
        if (!CheckNullEmptyBlank.check(user.getUsername())
                & !(user.getUsername().equals(userInDb.getUsername()))) {

            userInDb.setUsername(user.getUsername());

        }
        if (!CheckNullEmptyBlank.check(user.getTelephone())
                & !(user.getTelephone().equals(userInDb.getTelephone()))) {

            userInDb.setTelephone(user.getTelephone());

        }
        if (!CheckNullEmptyBlank.check(user.getPassword())) {

            userInDb.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        }
        List<User> usersList = userRepository.checkIfUserDetailsIstaken(userid, userInDb.getUsername(),
                userInDb.getEmail(), userInDb.getTelephone());
        if (usersList.isEmpty()) {
            userInDb = userRepository.save(userInDb);
            CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.NO_CONTENT.value(),
                    ApplicationConsts.I204_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC,ApplicationConsts.timeNow(), userInDb);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }
        throw new DataIntegrityViolationException("A USER WITH THE DETAILS EXIST");
    }

    public ResponseEntity<CustomResponseStatus> deleteUser(String userid) throws ResourceNotFoundException {
        Optional<User> userInDb = userRepository.findById(userid);
        
        if (userInDb.isPresent()) {
            try {
                userRepository.deleteById(userid);
                CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.NO_CONTENT.value(),
                        ApplicationConsts.I200_MSG,
                        ApplicationConsts.SUCCESS,
                        ApplicationConsts.SRC, ApplicationConsts.timeNow(), null);
                return new ResponseEntity<>(status, HttpStatus.OK);

            } catch (Exception e) {
                CustomResponseStatus status = CustomResponseStatus.build(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ApplicationConsts.E500_MSG,
                        ApplicationConsts.FAILURE,
                        e.getClass().getName(),ApplicationConsts.timeNow(), null);
                return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new ResourceNotFoundException("User with id :" + userid + " not found!");

    }

}
