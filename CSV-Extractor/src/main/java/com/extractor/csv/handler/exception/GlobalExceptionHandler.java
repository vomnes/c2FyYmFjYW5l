// package com.extractor.csv.handler.exception;

// import java.net.http.HttpHeaders;

// import com.extractor.csv.lib.ResponseHTTP;

// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.MissingServletRequestParameterException;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.context.request.WebRequest;

// @Order(Ordered.HIGHEST_PRECEDENCE)
// @ControllerAdvice
// public class GlobalExceptionHandler  {

//     @ExceptionHandler(MissingServletRequestParameterException.class)
//     // @ExceptionHandler(Exception.class)
//     protected final ResponseEntity<Object> handleMissingValue(
//     MissingServletRequestParameterException ex,
//        HttpHeaders headers, HttpStatus status, WebRequest request) {
//         System.out.println("Hello WOrld");
//        String error = "The parameter " + ex.getParameterName() + " is missing.";
//        return new ResponseHTTP().WithError(error, HttpStatus.BAD_REQUEST);
//    }

//    //other exception handlers below
// }