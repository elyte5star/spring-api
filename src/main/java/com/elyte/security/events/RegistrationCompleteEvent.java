package com.elyte.security.events;
import java.util.Locale;
import org.springframework.context.ApplicationEvent;
import lombok.Getter;
import com.elyte.domain.User;


@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final Locale locale;
    private final User user;

    public RegistrationCompleteEvent(final User user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
       
    }
    
}
