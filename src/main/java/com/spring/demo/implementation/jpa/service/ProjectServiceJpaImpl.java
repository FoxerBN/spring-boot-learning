package com.spring.demo.implementation.jpa.service;

import com.spring.demo.api.ProjectService;
import com.spring.demo.api.UserService;
import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.ProjectAddRequest;
import com.spring.demo.api.request.ProjectEditRequest;
import com.spring.demo.domain.Project;
import com.spring.demo.domain.User;
import com.spring.demo.implementation.jpa.entity.ProjectEntity;
import com.spring.demo.implementation.jpa.entity.UserEntity;
import com.spring.demo.implementation.jpa.repository.ProjectJpaRepository;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Profile("jpa")
@Slf4j
public class ProjectServiceJpaImpl implements ProjectService {
    private final ProjectJpaRepository repository;
    private final UserService userService;

    public ProjectServiceJpaImpl(ProjectJpaRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Project get(long id) {
        return repository.findById(id)
                .map(this::mapProjectEntityToProject)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    @Override
    public List<Project> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapProjectEntityToProject)
                .toList();
    }

    @Override
    public List<Project> getAllByUser(long userId) {
        if (userService.get(userId) != null){
            return repository.findAllByUserId(userId)
                      .stream()
                      .map(this::mapProjectEntityToProject)
                      .toList();
        }
        return List.of();
    }

    @Override
    public void delete(long id) {
        if (this.get(id) != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public long add(ProjectAddRequest request) {
        final User user = userService.get(request.getUserId());
        final UserEntity userEntity = new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
        try {
            return repository.save(new ProjectEntity(userEntity, request.getName(), request.getDescription(), OffsetDateTime.now())).getId();
        }catch (DataIntegrityViolationException | ConstraintViolationException e) {
            throw new BadRequestException("Project with the same name already exists for this user or invalid data provided: " + e.getMessage());
        }catch (DataAccessException e) {
            log.error("Error while saving project: {}", e.getMessage());
            throw new RuntimeException("Failed to save project", e);
        }
    }

    @Override
    public void edit(long id, ProjectEditRequest request) {
        final ProjectEntity projectEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        projectEntity.setName(request.getName());
        projectEntity.setDescription(request.getDescription());

        repository.save(projectEntity);
    }

    private Project mapProjectEntityToProject(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Project(
                entity.getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }
}
