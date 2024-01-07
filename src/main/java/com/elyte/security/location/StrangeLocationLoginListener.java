package com.elyte.security.location;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
import com.elyte.domain.enums.EmailType;
import com.elyte.domain.request.EmailAlert;
import com.elyte.service.EmailAlertService;
import com.elyte.utils.UtilityFunctions;

////EVENT LISTENER

@Component
public class StrangeLocationLoginListener extends UtilityFunctions implements ApplicationListener<OnStrangeLocationLoginEvent> {

    @Autowired
    private EmailAlertService emailAlertService;
    
    private static final Logger log = LoggerFactory.getLogger(StrangeLocationLoginListener.class);

    @Override
    public void onApplicationEvent(OnStrangeLocationLoginEvent event) {

        log.info("Strange location event - " + event.getAppUrl());
        final String enableLocUri = event.getAppUrl() + "/users/enableNewLocation?token="
                + event.getNewLocationToken().getToken();
        final String changePassUri = event.getAppUrl() + "/users/changePassword";
        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEmailType(EmailType.UNUSUAL_LOCATION_LOGIN);
        emailAlert.setRecipientEmail(event.getEmail());
        emailAlert.setRecipientUsername(event.getUsername());
        emailAlert.setSubject("Login attempt from a different location");
        Map<String, Object> data = new HashMap<>();
        data.put("username",event.getUsername());
        data.put("ip",this.getClientIP());
        data.put("time",this.timeNow());
        data.put("country",event.getNewLocationToken().getUserLocation().getCountry());
        data.put("changePassUri",changePassUri );
        data.put("enableLocationLink",enableLocUri);
        emailAlert.setData(data);
        emailAlertService.sendEmailAlert(emailAlert, event.getLocale());

    }

    

}