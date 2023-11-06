package com.elyte.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.elyte.service.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {


    private AuthService authService;


    
}
