package com.spring.demo.implementation.jpa.service;

import com.spring.demo.api.ProjectService;
import com.spring.demo.api.TaskService;
import com.spring.demo.api.UserService;
import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.exception.InternalErrorException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.TaskAddRequest;
import com.spring.demo.api.request.TaskEditRequest;
import com.spring.demo.domain.Project;
import com.spring.demo.domain.Task;
import com.spring.demo.domain.TaskStatus;
import com.spring.demo.domain.User;
import com.spring.demo.implementation.jpa.entity.ProjectEntity;
import com.spring.demo.implementation.jpa.entity.TaskEntity;
import com.spring.demo.implementation.jpa.entity.UserEntity;
import com.spring.demo.implementation.jpa.repository.TaskJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
@Service
@Profile("jpa")
@Slf4j
public class TaskServiceJpaImpl implements TaskService {
    private final TaskJpaRepository repository;
    private final UserService userService;
    private final ProjectService projectService;

    public TaskServiceJpaImpl(TaskJpaRepository repository, UserService userService, ProjectService projectService) {
        this.repository = repository;
        this.userService = userService;
        this.projectService = projectService;
    }

    @Override
    public long add(TaskAddRequest request) {
        final User user = userService.get(request.getUserId());
        final UserEntity userEntity = new UserEntity(user.getId(), user.getName(), user.getEmail());
        final ProjectEntity projectEntity;

        if (request.getProjectId() != null){
            final Project project = projectService.get(request.getProjectId());
            projectEntity = new ProjectEntity(project.getId(), userEntity, project.getName(), project.getDescription(), project.getCreatedAt());
        }else {
            projectEntity = null;
        }
        try {
            return repository.save(new TaskEntity(
                    userEntity,
                    projectEntity,
                    request.getName(),
                    request.getDescription(),
                    TaskStatus.NEW,
                    OffsetDateTime.now()
            )).getId();
        }catch (DataAccessException e){
            log.error("Error while adding task: {}", e.getMessage());
            throw new InternalErrorException("Failed to add task due to database error.");
        }
    }

    @Override
    public void edit(long id, TaskEditRequest request) {
        final TaskEntity taskEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskEntity.setName(request.getName());
        taskEntity.setDescription(request.getDescription());
        taskEntity.setStatus(TaskStatus.valueOf(request.getStatus()));
        repository.save(taskEntity);
    }

    @Override
    public void changeStatus(long id, TaskStatus status) {
        final TaskEntity taskEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskEntity.setStatus(status);
        repository.save(taskEntity);
    }

    @Override
    public void assignProject(long taskId, long projectId) {
        final TaskEntity taskEntity = repository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        final Project project = projectService.get(projectId);
        if (!taskEntity.getUser().getId().equals(project.getUserId())){
            throw new BadRequestException("Cannot assign task to project that does not belong to the same user.");
        }
        final ProjectEntity projectEntity = new ProjectEntity(
                project.getId(),
                taskEntity.getUser(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
        taskEntity.setProject(projectEntity);
        repository.save(taskEntity);
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public Task get(long id) {
        return repository.findById(id)
                .map(this::mapTaskEntityToTask)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Override
    public List<Task> getAll() {
        return repository.findAll().stream()
                .map(this::mapTaskEntityToTask)
                .toList();
    }

    @Override
    public List<Task> getAllByUserId(long userId) {
        if (userService.get(userId) != null){
            return repository.findAllByUserId(userId).stream()
                    .map(this::mapTaskEntityToTask)
                    .toList();
        }
        return null;
    }

    @Override
    public List<Task> getAllByProjectId(long projectId) {
        if (projectService.get(projectId) != null){
            return repository.findAllByProjectId(projectId).stream()
                    .map(this::mapTaskEntityToTask)
                    .toList();
        }
        return null;
    }

    private Task mapTaskEntityToTask(TaskEntity taskEntity) {
        return new Task(
                taskEntity.getId(),
                taskEntity.getUser().getId(),
                taskEntity.getProject() != null ? taskEntity.getProject().getId() : null,
                taskEntity.getName(),
                taskEntity.getDescription(),
                taskEntity.getStatus(),
                taskEntity.getCreatedAt()
        );
    }
}
