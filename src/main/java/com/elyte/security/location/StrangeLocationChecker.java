package com.elyte.security.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import com.elyte.domain.NewLocationToken;
import com.elyte.exception.UnusualLocationException;
import com.elyte.security.UserPrincipal;
import com.elyte.service.UserService;
import org.springframework.context.ApplicationEventPublisher;

//UNUSUAL LOCATION PUBLISHER

@Component
public class StrangeLocationChecker {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void checkDifferentLocation(UserPrincipal userDetails) {
        final String ip = this.getClientIP();
        final NewLocationToken token = userService.isNewLocationLogin(userDetails.getUsername(), ip);
        if (token != null) {
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath();
            applicationEventPublisher.publishEvent(new OnStrangeLocationLoginEvent(request.getLocale(),
                    userDetails.getUser().getEmail(), userDetails.getUsername(), ip, token, appUrl));
            throw new UnusualLocationException("Unusual location");
        }

    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() ||
                !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
        // return "128.101.101.101"; // for testing United States
        // return "41.238.0.198"; // for testing Egypt
    }

}
