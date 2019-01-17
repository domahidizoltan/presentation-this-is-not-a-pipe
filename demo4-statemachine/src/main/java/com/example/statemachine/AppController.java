package com.example.statemachine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Instant;

@RestController
public class AppController {

    @Value("${remote-api-url}")
    private String url;

    private RestTemplate restTemplate;
    private JavaMailSender mailSender;

    AppController(RestTemplate restTemplate, JavaMailSender mailSender) {
        this.restTemplate = restTemplate;
        this.mailSender = mailSender;
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> start(@PathVariable("id") Integer id, @RequestBody String body) {
        String startUrl = url + "/start/" + id;
        return exchangeRequest(startUrl, body);
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<String> stop(@PathVariable("id") Integer id) {
        String stopUrl = url + "/stop/" + id;
        return exchangeRequest(stopUrl, null);
    }

    @PostMapping("/send-mail/{id}")
    public void sendMail(@PathVariable("id") Integer id) throws MessagingException {
        System.out.println("Sending mail to " + id);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("machine@mail.com");
        helper.setTo(id + "@mail.com");
        helper.setText(id + " stopped at " + Instant.now());

        mailSender.send(message);
    }

    private ResponseEntity<String> exchangeRequest(String url, String body) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, body, String.class);
            return ResponseEntity
                .status(response.getStatusCode())
                .body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getResponseBodyAsString());
        }
    }

}
