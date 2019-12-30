package com.extractor.csv;

import java.io.IOException;

import com.extractor.csv.lib.ResponseHTTP;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ExtractController {
  private CSV data;

  @RequestMapping(value = "/uploadCSV", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<?> downloadFile(
    @RequestParam(value = "file", required = false) MultipartFile file,
    @RequestHeader(value = "Content-Test", required = false) String isTest
    ) {
    // Manage the error if no files are uploaded | Not possible to use ExceptionHandler for MultipartFile
    if (file == null) {
      return new ResponseHTTP().WithError("No CSV file selected", HttpStatus.BAD_REQUEST);
    }
    // Check file type
    if (!file.getContentType().equals("text/csv")) {
      return new ResponseHTTP().WithError("Not a CSV file type - " + file.getContentType(), HttpStatus.BAD_REQUEST);
    }
    try {
      java.io.Reader in = new java.io.InputStreamReader(file.getInputStream());
      data = new CSV(in);
      this.readFile();
      if (!data.getHasEmailOrPhoneNumber()) {
        return new ResponseHTTP().WithError("The CSV file must at least contains a valid 'email' or 'phone number'", HttpStatus.NOT_ACCEPTABLE);
      }
      if (isTest != null && isTest.equals("true")) {
        // Testing purposes
        return new ResponseHTTP().WithJSON(data.getCSVDataJSON(), HttpStatus.CREATED);
      } else {
        return sendDataToStoreContacts();
      }
    } catch (IOException e) {
      return new ResponseHTTP().WithError("An error has occured", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void readFile() throws IOException {
    try {
      while (this.data.getNextLine() != null);
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  private ResponseEntity<?> sendDataToStoreContacts() {
    HttpEntity<String> entity = new HttpEntity<String>(this.data.getCSVDataJSON().toString(), null);

    RestTemplate restTemplate = new RestTemplate();
    try {
      return restTemplate.postForEntity("http://service-2:8081/v1/contacts", entity, String.class);
    } catch (HttpClientErrorException e) {
      return ResponseEntity
      .status(e.getRawStatusCode())
      .headers(e.getResponseHeaders())
      .body(e.getResponseBodyAsString());
    }
  }
}