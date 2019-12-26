package com.extractor.csv;

import com.extractor.csv.lib.ResponseHTTP;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ExtractController {
  // @ExceptionHandler(MissingServletRequestParameterException.class)
  // public void handleMissingParams(MissingServletRequestParameterException ex) {
  //     String name = ex.getParameterName();
  //     System.out.println(name + " parameter is missing");
  //     // Actual exception handling
  // }

  @RequestMapping(value = "/csv", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<?> downloadFile(@RequestParam("file") MultipartFile file) {
    // Check file type
    if (!file.getContentType().equals("text/csv")) {
      return new ResponseHTTP().WithError("Not a CSV file type - " + file.getContentType(), HttpStatus.BAD_REQUEST);
    }
    try {
      java.io.Reader in = new java.io.InputStreamReader(file.getInputStream());
      CSV data = new CSV(in);
      while (data.getNextLine() != null);
      System.out.println("OK");
    } catch (IllegalArgumentException e) {
      return new ResponseHTTP().WithError("Invalid arguments", HttpStatus.NOT_ACCEPTABLE);
    } catch (IOException e) {
      return new ResponseHTTP().WithError("This is an error", HttpStatus.NOT_ACCEPTABLE);
    }
    return new ResponseEntity<String>(HttpStatus.OK);
  }
}