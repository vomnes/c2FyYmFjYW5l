package com.extractor.csv.lib;

public class Validator {
    // Return true if the parameter string is a valid email
    public Boolean isEmail(String s) {
        return s.matches("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$)");
    }
    // Return true if the parameter string is a valid french phone number
    public Boolean isPhoneNumberFR(String s) {
        return s.matches("(^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$)");
    }
}