package com.elyte.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import java.time.Duration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

public class ApplicationConsts {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    private static final Logger log = LoggerFactory.getLogger(ApplicationConsts.class);

    // common messages
    public final String SRC = "0";
    public final String ARC = "1";
    public final String ARC_MSG = "Authentication required.";
    public final String ADR_MSG = "Admin right required.";
    public final String FRC = "2";
    public final String SEC = "0";
    public final String FEC = "2";
    public final boolean SUCCESS = true;
    public final boolean FAILURE = false;
    public final String I201_MSG = "Entity created successful";

    public final String I200_MSG = "Operation successful";

    public final String I200_MSG_LOC = "Operation successful,Location Validated";

    public final String I202_MSG = "Input validation failed";

    public final String I203_MSG = "Operation failed";

    public final String I204_MSG = "Entity updated";

    public final String SMTP_MSG = "Hello %s,\n\nYour OTP for registration is %s. It is valid for %s minutes. Do not share it with anyone."
            +
            "\n\nRegards,\nTeam ELYTE.\n\n\nThis is system generated mail. Please do not reply to this.";

    public final String VERIFY_USER_EMAIL_TEMPLATE_NAME = "html/verify-user";

    public final String RESET_USER_PASSWORD = "html/reset-password";

    public final String EMAIL_WITHATTACHMENT_TEMPLATE_NAME = "html/email-withattachment";

    public final String EMAIL_TEXT_TEMPLATE_NAME = "text/email-text";

    public final String E205_MSG = "Data integrity violation";

    public final String E400_MSG = "Malformed request syntax";

    public final String E401_MSG = "Oops! You have entered invalid username/password.";

    public final String E402_MSG = "User already exists!";

    public final String E404_MSG = "Entity not found.";

    public final String E403_SMTP_MSG = "Your OTP is expired.";

    public final String E401_SMTP_MSG = "Your OTP is invalid.";

    public final String E410_SMTP_MSG = "Your OTP is no longer valid or usable.";

    public final String I999_MSG = "Sorry! Something went wrong. Please try again.";

    public final String E423_MSG = "User account disabled/locked";

    public String E413_MSG = "Larger than limits defined by server";

    public final String E500_MSG = "Internal Server Error.";

    public ObjectMapper mapper;

    public ApplicationConsts() {

        this.mapper = new ObjectMapper();

    }

    public String timeNow() {
        LocalDateTime current = LocalDateTime.now();
        return current.format(dtf);

    }

    public String convertObjectToJson(Object object) {
        String result = null;
        try {
            result = this.mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("[x] JsonProcessingException Exception ", e.getLocalizedMessage());

        }
        return result;
    }

    public long diff(String start, String end) {
        LocalDateTime dateTime1 = LocalDateTime.parse(start, dtf);
        LocalDateTime dateTime2 = LocalDateTime.parse(end, dtf);
        Duration duration = Duration.between(dateTime1, dateTime2);
        return Math.abs(duration.toSeconds());
    }

}
