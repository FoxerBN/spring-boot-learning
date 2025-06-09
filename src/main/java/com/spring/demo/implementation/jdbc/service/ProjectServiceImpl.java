package com.spring.demo.implementation.jdbc.service;

import com.spring.demo.api.ProjectService;
import com.spring.demo.api.request.ProjectAddRequest;
import com.spring.demo.api.request.ProjectEditRequest;
import com.spring.demo.domain.Project;
import com.spring.demo.implementation.jdbc.repository.ProjectJdbcRepository;
import com.spring.demo.implementation.jdbc.repository.TaskJdbcRepository;
import com.spring.demo.implementation.jdbc.repository.UserJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Profile("jdbc")
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectJdbcRepository repository;
    private final UserJdbcRepository userJdbcRepository;
    private final TaskJdbcRepository taskJdbcRepository;

    @Override
    public Project get(long id) {
        return repository.getById(id);
    }

    @Override
    public List<Project> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Project> getAllByUser(long userId) {
        if(userJdbcRepository.getById(userId) != null) {
            return repository.getAllByUser(userId);
        }
        return new ArrayList<>();
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            taskJdbcRepository.deleteAllByProject(id);
            repository.deleteProject(id);
        }
    }

    @Override
    public long add(ProjectAddRequest request) {
        return repository.addProject(request);
    }

    @Override
    public void edit(long id, ProjectEditRequest request) {
        if (this.get(id) != null) {
            repository.updateProject(id, request);
        }
    }
}
