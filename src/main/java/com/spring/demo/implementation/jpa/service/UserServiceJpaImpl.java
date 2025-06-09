package com.spring.demo.implementation.jpa.service;

import com.spring.demo.api.UserService;
import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.UserAddRequest;
import com.spring.demo.domain.User;
import com.spring.demo.implementation.jpa.entity.UserEntity;
import com.spring.demo.implementation.jpa.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("jpa")
@Slf4j
public class UserServiceJpaImpl implements UserService {

    private final UserJpaRepository repository;

    public UserServiceJpaImpl(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public long add(UserAddRequest request) {
        try {
            return repository.save(new UserEntity(request.getName(), request.getEmail())).getId();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        } catch (DataAccessException e){
            log.error("Error accessing data while inserting user: {}", request, e);
            throw new RuntimeException("Error accessing data while inserting user", e);
        }
    }
    @Override
    public void delete(long id) {
        if (this.get(id) != null){
            repository.deleteById(id);
        }
    }

    @Override
    public User get(long id) {
        return repository.findById(id).map(this::mapUserEntityToUser)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public List<User> getAll() {
        return repository.findAll().stream().map(this::mapUserEntityToUser)
                .toList();
    }

    private User mapUserEntityToUser(UserEntity entity) {
        return new User(entity.getId(), entity.getName(), entity.getEmail());
    }
}
