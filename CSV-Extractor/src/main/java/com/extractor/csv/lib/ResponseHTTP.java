package com.extractor.csv.lib;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHTTP {
    public ResponseEntity<Object> WithError(String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("Content-Type", "application/json");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Error", message);
        return new ResponseEntity<>(jsonResponse.toString(), headers, status);
    }

    public ResponseEntity<Object> Empty(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        return new ResponseEntity<>(headers, status);
    }

    public ResponseEntity<Object> WithJSON(JSONArray content, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("Content-Type", "application/json");
        headers.add("Content-Test", "true");
        return new ResponseEntity<>(content.toString(), headers, status);
    }

    public ResponseEntity<Object> WithJSON(JSONObject content, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(content.toString(), headers, status);
    }
}