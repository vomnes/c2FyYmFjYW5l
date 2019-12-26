package com.extractor.csv.lib;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.json.JSONObject;

public class ResponseHTTP {
    public ResponseEntity<?> WithError(String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("Content-Type", "application/json");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Status", status.value());
        jsonResponse.put("Error", message);
        return new ResponseEntity<>(jsonResponse.toString(), headers, status);
    }
}