package com.elyte.utils;

public class CheckNullEmptyBlank {

    public static boolean check(String strToCheck) {
        // check whether the given string is null or not
        if (strToCheck == null) {
            return false;
        }
        // check whether the given string is empty or not
        else if (strToCheck.isEmpty()) {
            return false;
        }
        // check whether the given string is blank or not
        else return !strToCheck.isBlank();
    }

}
