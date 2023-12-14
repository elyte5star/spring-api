package com.elyte.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.elyte.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.ui.Model;



/**
 * Application home page and login.
 */
@Controller
public class MainController {

    @Autowired
    private AuthenticationManager authenticationManager;

    /* Home page. */
    @GetMapping(value = { "/", "/index" })
    public String index() {
        return "index";
    }


    /** Administration zone index. */
    @RequestMapping("/admin/index.html")
    public String adminIndex() {
        return "admin/index";
    }

    /* Login page. */
    @GetMapping(value= "/login")
    public String login() {
        return "login";
    }

    /** Login form with error. */
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }


     /* error page. */
    @GetMapping(value= "/error")
    public String error() {
        return "error";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "403";
    }

    @PostMapping("/login")
    public void login(HttpServletRequest request,
            @RequestBody @Valid LoginRequest loginRequest) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.username(), loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        final UserPrincipal userDetails = (UserPrincipal) authenticationResponse.getPrincipal();
        
    }

    public record LoginRequest(String username, String password) {
    }

}
