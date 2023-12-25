package com.elyte.exception;

import org.springframework.amqp.core.Message;
import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogMessageExceptionHandler implements MessageExceptionHandler{
    private static final Logger log = LoggerFactory.getLogger(LogMessageExceptionHandler.class);
    
    @Override
    public void handle(Message message, Throwable cause) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        log.warn("Dead letter message from queue  , message  , headers  :  cause",
        headers.get("x-original-queue"), getMessageString(message), headers, cause);
    }

    protected String getMessageString(Message message) {
        String contentType = message.getMessageProperties() != null?message.getMessageProperties().getContentType():null;
        if("text/plain".equals(contentType) || "application/json".equals(contentType) || "text/x-json".equals(contentType) || "application/xml".equals(contentType)) {
            return new String(message.getBody());
        }
        else {
            return Arrays.toString(message.getBody()) + "(byte[" + message.getBody().length + "])";
        }
    }
    
}
