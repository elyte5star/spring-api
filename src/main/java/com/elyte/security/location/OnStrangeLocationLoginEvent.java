package com.elyte.security.location;
import java.util.Locale;
import org.springframework.context.ApplicationEvent;
import com.elyte.domain.NewLocationToken;
import lombok.Getter;


//STRANGE LOGIN EVENT(DIFFERENT LOCATION)
@Getter
public class OnStrangeLocationLoginEvent  extends ApplicationEvent{

    private final Locale locale;
    private final String username;
    private final String email;
    private final String ip;
    private final NewLocationToken newLocationToken;
    private final String appUrl;

    public OnStrangeLocationLoginEvent(Locale locale,String email,String username,String ip,NewLocationToken newLocationToken,String appurl) {
        super(newLocationToken);
        this.locale= locale;
        this.username=username;
        this.ip=ip;
        this.newLocationToken=newLocationToken;
        this.appUrl=appurl;
        this.email=email;
    }

}
