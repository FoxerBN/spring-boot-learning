package com.spring.demo.api;

import com.spring.demo.api.request.UserAddRequest;
import com.spring.demo.domain.User;

import java.util.List;

public interface UserService {
    long add(UserAddRequest request);
    void delete(long id);
    User get(long id);
    List<User> getAll();
}
