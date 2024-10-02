package com.elyte.security.events;

import java.util.Locale;
import org.springframework.context.ApplicationEvent;
import com.elyte.domain.User;
import com.elyte.domain.request.EmailAlert;

import lombok.Getter;


@Getter
public class GeneralUserEvent extends ApplicationEvent{

    private final User user;
    private final Locale locale;
    private final EmailAlert mailObject;

    public GeneralUserEvent(final EmailAlert mailObject,final User user, final Locale locale) {
        super(mailObject);
        this.user = user;
        this.locale = locale;
        this.mailObject=mailObject;
    }
    
}
