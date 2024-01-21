package com.elyte.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Application home page and login.
 */
@Controller
public class MainController {

    
    /* Home page. */
    @GetMapping(value = { "/", "/index" })
    public String notFound() {
        return "404";
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

    
}
