package com.assisisolutions.blog.wiremockexamples;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Service
public class UserService {

    private String baseUrl;

    private RestTemplate restTemplate;

    public UserService(@Value("${userservice.url}") String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    public List<User> getUsers() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() { });
        return responseEntity.getBody();
    }

    public List<User> getUsersByName(String username) {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(
                baseUrl + "/users?name={name}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() { },
                username);
        return responseEntity.getBody();
    }

    public String createUser(String username) {
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.POST,
                new HttpEntity<>(new User(username)),
                Void.class);
        URI location = responseEntity.getHeaders().getLocation();
        int p = location.getPath().lastIndexOf('/');
        return location.getPath().substring(p + 1);
    }
}
