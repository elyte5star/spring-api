package com.elyte.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApplicationConsts {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // common messages
    public static final String SRC = "0";
    public static final String ARC = "1";
    public static final String ARC_MSG = "Authentication required.";
    public static final String ADR_MSG = "Admin right required.";
    public static final String FRC = "2";
    public static final String SEC = "0";
    public static final String FEC = "2";
    public static final boolean SUCCESS = true;
    public static final boolean FAILURE = false;
    public static final String I201_MSG = "Entity created successful";

    public static final String I200_MSG = "Operation successful";

    public static final String I202_MSG = "Input validation failed";

    public static final String I203_MSG = "Operation failed";

    public static final String I204_MSG = "Entity updated";

    

    public static final String E205_MSG = "Data integrity violation";

    public static final String E400_MSG = "Malformed request syntax";

    public static final String E401_MSG = "Oops! You have entered invalid username/password.";

    public static final String E402_MSG = "User already exists!";

    public static final String E404_MSG = "Entity not found.";

    public static final String I999_MSG = "Sorry! Something went wrong. Please try again.";

    public static final String E409_MSG = "User account disabled";

    public static final String E500_MSG = "Internal Server Error.";

    public static String timeNow(){
        LocalDateTime current = LocalDateTime.now();
        return  current.format(dtf);

    }

    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}
