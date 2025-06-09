package com.spring.demo;

import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.UserAddRequest;
import com.spring.demo.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class UserIntegrationTests extends IntegrationTest {
    @Test
    public void getAllUsers() {
        final ResponseEntity<List<User>> userResponse = restTemplate.exchange(
                "/user",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        Assertions.assertNotNull(userResponse.getBody());
        Assertions.assertTrue(userResponse.getBody().size() >= 2);
    }

    @Test
    public void insertUser() {
        insertUser(generateRandomUser());
    }

    @Test
    public void deleteUser(){
        final UserAddRequest request = generateRandomUser();
        final long id = insertUser(request);

        final ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/user/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        Assertions.assertNull(deleteResponse.getBody());
        ResponseEntity<ResourceNotFoundException> getResponse = restTemplate.getForEntity(
                "/user/" + id,
                ResourceNotFoundException.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void existingEmailInsert(){
        final UserAddRequest request = generateRandomUser();
        final long id = insertUser(request);

        final ResponseEntity<BadRequestException> badRequest = restTemplate.postForEntity(
                "/user",
                request,
                BadRequestException.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, badRequest.getStatusCode());
    }

    @Test
    public void getUser(){
        final UserAddRequest request = generateRandomUser();
        final long id = insertUser(request);

        final ResponseEntity<User> user = restTemplate.getForEntity(
                "/user/" + id,
                User.class
        );
        Assertions.assertEquals(HttpStatus.OK, user.getStatusCode());
        Assertions.assertNotNull(user.getBody());

        final User userBody = user.getBody();
        Assertions.assertEquals(id, userBody.getId());
        Assertions.assertEquals(request.getName(), userBody.getName());
        Assertions.assertEquals(request.getEmail(), userBody.getEmail());
    }

    private UserAddRequest generateRandomUser() {
        return new UserAddRequest(
                "name" + System.currentTimeMillis(),
                "email" + System.currentTimeMillis() + "@example.com"
        );
    }


    private long insertUser(UserAddRequest request) {
        final ResponseEntity<Map> insertResponse = restTemplate.postForEntity(
                "/user",
                request,
                Map.class
        );

        Object idObj = insertResponse.getBody().get("id");
        long id = Long.parseLong(idObj.toString());
        Assertions.assertEquals(HttpStatus.CREATED, insertResponse.getStatusCode());
        Assertions.assertNotNull(idObj);
        return id;
    }
}
