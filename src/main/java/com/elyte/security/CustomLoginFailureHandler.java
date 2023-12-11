// package com.elyte.security;

// import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;


// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.LockedException;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.stereotype.Component;
// import com.elyte.domain.User;
// import com.elyte.repository.UserRepository;


// @Component
// public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler{
    
//     @Autowired
//     private  LoginAttemptService loginAttemptService;

//     @Autowired
//     private UserRepository userRepository;


//     @Override
//     public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//             AuthenticationException exception) throws IOException, ServletException {
//         String username = request.getParameter("username");
//         User user = userRepository.findByUsername(username);
         
//         if (user != null) {
//             if (user.isEnabled() && user.isAccountNonLocked()) {
//                 if (user.getFailedAttempt() < LoginAttemptService.MAX_ATTEMPT - 1) {
//                    //loginAttemptService.increaseFailedAttempts(user);
//                 } else {
//                     //loginAttemptService.lock(user);
//                     exception = new LockedException("Your account has been locked due to 3 failed attempts."
//                             + " It will be unlocked after 24 hours.");
//                 }
//             } else if (!user.isAccountNonLocked()) {
//                 if (loginAttemptService.unlockWhenTimeExpired(user)) {
//                     exception = new LockedException("Your account has been unlocked. Please try to login again.");
//                 }
//             }
             
//         }
         
//         //super.setDefaultFailureUrl("/error");
//         super.onAuthenticationFailure(request, response, exception);
//     }


// }
