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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        ResponseEntity<String> response = this.restTemplate.
        postForEntity("http://localhost:" + port + "/uploadCSV", null, String.class);
        assertThat(response.getBody())
            .contains("{\"Error\":\"No CSV file selected\"}");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void ExtractControllerUploadCSV_ErrorNotACSVFile() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp("not-csv.pdf", "empty"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("{\"Error\":\"Not a CSV file type - application\\/pdf\"}");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void ExtractControllerUploadCSV_ErrorMustContainsAtLeastAValidEmailOrPhoneNumber_InvalidEmail() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp("invalid-data.csv", "Email;Nom\nv.co;Valentin\n"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("{\"Error\":\"The CSV file must at least contains a valid 'email' or 'phone number'\"}");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void ExtractControllerUploadCSV_ErrorMustContainsAtLeastAValidEmailOrPhoneNumber_InvalidPhoneNumber() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp("invalid-data.csv", "N° de mobile;Nom\nabcd;Valentin\n"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("{\"Error\":\"The CSV file must at least contains a valid 'email' or 'phone number'\"}");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void ExtractControllerUploadCSV_ValidCSV_WithFieldsName() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Content-Test", "true");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp("valid-data-with-fields-name.csv", "Email;Nom\nv@v.co;Valentin\na@a.co;Hello"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("[{\"Nom\":\"Valentin\",\"email\":\"v@v.co\"},{\"Nom\":\"Hello\",\"email\":\"a@a.co\"}]");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void ExtractControllerUploadCSV_ValidCSV_WithoutFieldsName() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Content-Test", "true");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp(
            "valid-data-without-fields-name.csv",
            "+33699999999;Valentin\n0288888888;Hello"
            )
        );

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("[{\"phoneNumber\":\"+33699999999\",\"col2\":\"Valentin\"},{\"phoneNumber\":\"0288888888\",\"col2\":\"Hello\"}]");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void ExtractControllerUploadCSV_ValidCSV_WithFieldsName_Named() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Content-Test", "true");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp(
            "valid-data-with-fields-name.csv",
            "N° de mobile;Nom\n+33699999999;Valentin\n0288888888;Hello"
            )
        );

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("[{\"phoneNumber\":\"+33699999999\",\"Nom\":\"Valentin\"},{\"phoneNumber\":\"0288888888\",\"Nom\":\"Hello\"}]");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void ExtractControllerUploadCSV_ValidCSV_WithFieldsName_SkipInvalidEmail() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Content-Test", "true");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", myFiles.CreateTmp(
            "valid-data-with-invalid-email.csv",
            "Email;Nom\na.co;Valentin\ntest@test.fr;testName"
            )
        );

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

        ResponseEntity<String> response = this.restTemplate
        .postForEntity("http://localhost:" + port + "/uploadCSV", request, String.class);
        assertThat(response.getBody())
            .contains("[{\"Nom\":\"Valentin\"},{\"Nom\":\"testName\",\"email\":\"test@test.fr\"}]");
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
    }
}