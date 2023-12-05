package com.elyte.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.security.UserPrincipal;
import com.elyte.utils.ApplicationConsts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class MainController {

    @Autowired
    private AuthenticationManager authenticationManager;

    /* Home page. */
    @GetMapping(value = { "/", "/index" })
    public String index() {
        return "index";
    }

    /* Login page. */
    @GetMapping(value= "/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponseStatus> login(HttpServletRequest request,
            @RequestBody @Valid LoginRequest loginRequest) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        final UserPrincipal userDetails = (UserPrincipal) authenticationResponse.getPrincipal();
        CustomResponseStatus resp = CustomResponseStatus.build(HttpStatus.OK.value(),
                ApplicationConsts.I200_MSG,
                ApplicationConsts.SUCCESS,
                request.getRequestURL().toString(), ApplicationConsts.timeNow(), userDetails);
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    public record LoginRequest(String username, String password) {
    }

}
