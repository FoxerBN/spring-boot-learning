package com.spring.demo.api;

import com.spring.demo.api.request.TaskAddRequest;
import com.spring.demo.api.request.TaskEditRequest;
import com.spring.demo.domain.Task;
import com.spring.demo.domain.TaskStatus;

import java.util.List;

public interface TaskService {
    long add(TaskAddRequest request);
    void edit (long id, TaskEditRequest request);
    void changeStatus(long id, TaskStatus status);
    void assignProject(long taskId, long projectId);
    void delete(long id);
    Task get(long id);

    List<Task> getAll();
    List<Task> getAllByUserId(long userId);
    List<Task> getAllByProjectId(long projectId);
}
