package com.extractor.csv;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.extractor.csv.lib.File;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExtractControllerTests {

    @LocalServerPort
    private int port;
  
    @Autowired
    private TestRestTemplate restTemplate;

    private File myFiles = new File();
    
    // private ExtractController m;

    // @BeforeEach
    // void init() {
    //     m = new ExtractController();
    // }

    @Test
    public void ExtractControllerUploadCSV_ErrorNoFile() throws IOException {
        String entity = this.restTemplate.
        postForObject("http://localhost:" + port + "/uploadCSV", null, String.class);
        assertThat(entity.toString()).contains("{\"Erraor\":\"No CSV file selected\"}");
    }

    @Test
    public void ExtractControllerUploadCSV_ErrorNotACSVFile() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp("not-csv.pdf", "empty"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        assertThat(
            this.restTemplate
            .postForObject("http://localhost:" + port + "/uploadCSV", request, String.class)
        )
        .contains("{\"Error\":\"Not a CSV file type - application\\/pdf\"}");
    }

    @Test
    public void ExtractControllerUploadCSV_ErrorMustContainsAtLeastAValidEmailOrPhoneNumber() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp("invalid-data.csv", "Email;Nom\nv.co;Valentin\n"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        assertThat(
            this.restTemplate
            .postForObject("http://localhost:" + port + "/uploadCSV", request, String.class)
        )
        .contains("{\"Error\":\"The CSV file must at least contains a valid 'email' or 'phone number'\"}");
    }
}