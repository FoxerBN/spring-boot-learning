package com.spring.demo.implementation.jpa.repository;

import com.spring.demo.implementation.jpa.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByUserId(long userId);
    List<TaskEntity> findAllByProjectId(long projectId);
}
