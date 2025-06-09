package com.spring.demo.controller;

import com.spring.demo.api.UserService;
import com.spring.demo.api.request.UserAddRequest;
import com.spring.demo.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        return ResponseEntity.ok().body(userService.getAll());
    }
    @GetMapping("{id}")
    public ResponseEntity<User> getById(@PathVariable("id") long id){
        return ResponseEntity.ok().body(userService.get(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody UserAddRequest request) {
        long id = userService.add(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "User " + request.getName() + " was successfully created");
        response.put("id", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {

        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
