package com.elyte.exception;
import org.springframework.security.core.AuthenticationException;

import java.io.Serial;


public class UnusualLocationException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = 5861310537366287163L;

    public UnusualLocationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnusualLocationException(final String message) {
        super(message);
    }
}
