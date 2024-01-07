// package com.elyte.security.location;
// import java.util.Date;
// import org.slf4j.Logger;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.ApplicationListener;
// import org.springframework.context.MessageSource;
// import org.springframework.stereotype.Component;
// import org.slf4j.LoggerFactory;
// import com.elyte.domain.request.EmailAlert;
// import com.elyte.service.EmailAlertService;

// ////EVENT LISTENER

// @Component
// public class StrangeLocationLoginListener implements ApplicationListener<OnStrangeLocationLoginEvent> {

//     @Autowired
//     private MessageSource messages;

//     @Autowired
//     private EmailAlertService emailAlertService;
    
//     private static final Logger log = LoggerFactory.getLogger(StrangeLocationLoginListener.class);

//     @Override
//     public void onApplicationEvent(OnStrangeLocationLoginEvent event) {

//         log.info("Received spring custom event - " + event.getAppUrl());

//         final String enableLocUri = event.getAppUrl() + "/users/enableNewLocation?token="
//                 + event.getNewLocationToken().getToken();
//         final String changePassUri = event.getAppUrl() + "/users/changePassword.html";

//         EmailAlert mailObject = new EmailAlert(event.getEmail(), event.getUsername(),
//                 "Login attempt from a different location");
//         final String message = messages.getMessage("differentLocation",
//                 new Object[] { new Date().toString(), event.getNewLocationToken().getUserLocation().getCountry(),
//                         event.getIp(), enableLocUri, changePassUri },
//                 event.getLocale());

//         emailAlertService.sendStringMail(mailObject, message, event.getLocale());
//     }

// }