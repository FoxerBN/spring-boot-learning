package com.spring.demo.implementation.jdbc.service;

import com.spring.demo.api.UserService;
import com.spring.demo.api.request.UserAddRequest;
import com.spring.demo.domain.User;
import com.spring.demo.implementation.jdbc.repository.ProjectJdbcRepository;
import com.spring.demo.implementation.jdbc.repository.TaskJdbcRepository;
import com.spring.demo.implementation.jdbc.repository.UserJdbcRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Profile("jdbc")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJdbcRepository userJdbcRepository;
    private final ProjectJdbcRepository projectJdbcRepository;
    private final TaskJdbcRepository taskJdbcRepository;

    @Override
    public long add(UserAddRequest request) {
        return userJdbcRepository.add(request);
    }

    @Override
    public void delete(long id) {
        if(this.get(id) != null){
            taskJdbcRepository.deleteAllByUser(id);
            projectJdbcRepository.deleteAllByUser(id);
            userJdbcRepository.delete(id);
        }
    }

    @Override
    public User get(long id) {
        return userJdbcRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userJdbcRepository.getAll();
    }
}
