package com.spring.demo.implementation.jdbc.service;

import com.spring.demo.api.ProjectService;
import com.spring.demo.api.TaskService;
import com.spring.demo.api.UserService;
import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.request.TaskAddRequest;
import com.spring.demo.api.request.TaskEditRequest;
import com.spring.demo.domain.Project;
import com.spring.demo.domain.Task;
import com.spring.demo.domain.TaskStatus;
import com.spring.demo.implementation.jdbc.repository.TaskJdbcRepository;
import com.spring.demo.implementation.jdbc.repository.UserJdbcRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("jdbc")
public class TaskServiceImpl implements TaskService {

    private final TaskJdbcRepository repository;
    private final UserService userService;
    private final ProjectService projectService;

    public TaskServiceImpl(TaskJdbcRepository repository, UserService userService, ProjectService projectService) {
        this.repository = repository;
        this.userService = userService;
        this.projectService = projectService;
    }

    @Override
    public long add(TaskAddRequest request) {
        return repository.add(request);
    }

    @Override
    public void edit(long taskId, TaskEditRequest request) {
        if (this.get(taskId) != null){
            repository.update(taskId, request);
        }
    }

    @Override
    public void changeStatus(long id, TaskStatus status) {
        if (this.get(id) != null) {
            repository.updateStatus(id, status);
        }
    }

    @Override
    public void assignProject(long taskId, long projectId) {
        final Task task = this.get(taskId);
        final Project project = projectService.get(projectId);
        if (task != null && project != null) {
            if (task.getUserId() != project.getUserId()) {
                throw new BadRequestException("Task user and project user must be the same");
            }
            repository.updateProject(taskId, projectId);
        }
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            repository.delete(id);
        }
    }

    @Override
    public Task get(long taskId) {
        return repository.getById(taskId);
    }

    @Override
    public List<Task> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Task> getAllByUserId(long userId) {
        if (userService.get(userId) != null) {
            return repository.getAllByUserId(userId);
        }
        return List.of();
    }

    @Override
    public List<Task> getAllByProjectId(long projectId) {
        if (projectService.get(projectId) != null){
            return repository.getAllByProjectId(projectId);
        }
        return List.of();
    }
}

