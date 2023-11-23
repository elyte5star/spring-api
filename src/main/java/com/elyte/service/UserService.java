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
import com.elyte.domain.response.GetUsersResponse;
import com.elyte.domain.request.ModifyEntityRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import com.elyte.utils.CheckNullEmptyBlank;
import org.springframework.dao.DataIntegrityViolationException;
import com.elyte.domain.response.ModifyUserResponse;
import com.elyte.utils.CheckIfUserExist;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    LocalDateTime current = LocalDateTime.now();

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public ResponseEntity<GetUsersResponse> getUsers() {
        Iterable<User> allUsersInDb = userRepository.findAll();
        Status status = Status.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));
        GetUsersResponse usersResponse = GetUsersResponse.build(status, allUsersInDb);
        return new ResponseEntity<>(usersResponse, HttpStatus.OK);
    }

    public ResponseEntity<CreateUserResponse> addUser(CreateUserRequest createUserRequest)
            throws DataIntegrityViolationException {
        User newUser = new User();
        newUser.setUsername(createUserRequest.getUsername());
        newUser.setPassword(new BCryptPasswordEncoder().encode(createUserRequest.getPassword()));
        newUser.setTelephone(createUserRequest.getTelephone());
        newUser.setEmail(createUserRequest.getEmail());
        newUser.setLastLoginDate("0");
        newUser.setEnabled(createUserRequest.isEnabled());
        if (!CheckIfUserExist.isExisting(newUser, userRepository)) {
            Status status = Status.build(HttpStatus.CREATED.value(), ApplicationConsts.I201_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));
            CreateUserResponse response = CreateUserResponse.build(status, newUser.getUserid());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        }

        throw new DataIntegrityViolationException("A USER WITH THE DETAILS EXIST ALREADY");

    }

    public ResponseEntity<GetUserResponse> userById(String userid) throws ResourceNotFoundException {

        User user = userRepository.findByUserid(userid);

        if (user == null) {

            throw new ResourceNotFoundException("User with id :" + userid + " not found!");
        }
        Status status = Status.build(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));
        GetUserResponse getUserResponse = GetUserResponse.build(status, user);
        return new ResponseEntity<>(getUserResponse, HttpStatus.OK);

    }

    public ResponseEntity<ModifyUserResponse> updateUserInfo(ModifyEntityRequest user, String userid)
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
            Status status = Status.build(HttpStatus.NO_CONTENT.value(), ApplicationConsts.I204_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));
            ModifyUserResponse modifyUserResponse = new ModifyUserResponse();
            modifyUserResponse.setStatus(status);
            modifyUserResponse.setUser(userInDb);
            return new ResponseEntity<>(modifyUserResponse, HttpStatus.OK);
        }
        throw new DataIntegrityViolationException("A USER WITH THE DETAILS TAKEN");
    }

    public ResponseEntity<Status> deleteUser(String userid) throws ResourceNotFoundException {
        Optional<User> userInDb = userRepository.findById(userid);
        if (userInDb.isPresent()) {
            try {
                userRepository.deleteById(userid);
                Status status = Status.build(HttpStatus.NO_CONTENT.value(), ApplicationConsts.I200_MSG,
                        ApplicationConsts.SUCCESS,
                        ApplicationConsts.SRC, current.format(ApplicationConsts.dtf));
                return new ResponseEntity<>(status, HttpStatus.OK);

            } catch (Exception e) {
                userRepository.deleteById(userid);
                Status status = Status.build(HttpStatus.INTERNAL_SERVER_ERROR.value(), ApplicationConsts.E500_MSG,
                        ApplicationConsts.FAILURE,
                        e.getClass().getName(), current.format(ApplicationConsts.dtf));

                return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw new ResourceNotFoundException("User with id :" + userid + " not found!");

    }

}
