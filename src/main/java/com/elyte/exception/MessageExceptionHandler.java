package com.elyte.exception;

import org.springframework.amqp.core.Message;

public interface MessageExceptionHandler {

    void handle(Message message, Throwable cause);
    
}
