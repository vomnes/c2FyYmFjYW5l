package com.extractor.csv;

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
  @RequestMapping(value = "/csv", method = RequestMethod.POST, produces = "text/csv")
  @ResponseBody
  public ResponseEntity<String> downloadFile(
    @RequestParam("file") MultipartFile file) {
    // Check file type
    if (!file.getContentType().equals("text/csv")) {
      return new ResponseEntity<String>("Error: Not a CSV type file - " + file.getContentType(), HttpStatus.BAD_REQUEST);
    }
    try {
      java.io.Reader in = new java.io.InputStreamReader(file.getInputStream());
      CSV data = new CSV(in);
      while (data.getNextLine() != null);
      System.out.println("OK");
    } catch (IOException e) {
      return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
    }
    return new ResponseEntity<String>(HttpStatus.OK);
  }
}