package com.spring.demo;

import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.TaskAddRequest;
import com.spring.demo.api.request.TaskAssignStatusRequest;
import com.spring.demo.api.request.TaskChangeStatusRequest;
import com.spring.demo.api.request.TaskEditRequest;
import com.spring.demo.domain.Task;
import com.spring.demo.domain.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class TaskIntegrationTest extends IntegrationTest {
    @Test
    public void getAllTasks() {
        final ResponseEntity<List<Task>> taskResponse = restTemplate.exchange(
                "/task",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, taskResponse.getStatusCode());
        Assertions.assertNotNull(taskResponse.getBody());
        Assertions.assertFalse(taskResponse.getBody().isEmpty());
    }

    @Test
    public void getTaskById() {
        final ResponseEntity<Task> taskResponse = restTemplate.getForEntity(
                "/task/1",
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, taskResponse.getStatusCode());
        Assertions.assertNotNull(taskResponse.getBody());
        Assertions.assertEquals(1L, taskResponse.getBody().getId());
    }

    @Test
    public void getAllByUserId() {
        final ResponseEntity<List<Task>> taskResponse = restTemplate.exchange(
                "/task?userId=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, taskResponse.getStatusCode());
        Assertions.assertNotNull(taskResponse.getBody());
        Assertions.assertFalse(taskResponse.getBody().isEmpty());
    }

    @Test
    public void getAllByProjectId() {
        final ResponseEntity<List<Task>> taskResponse = restTemplate.exchange(
                "/task?projectId=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, taskResponse.getStatusCode());
        Assertions.assertNotNull(taskResponse.getBody());
        Assertions.assertFalse(taskResponse.getBody().isEmpty());
        Assertions.assertNotEquals(1L, taskResponse.getBody().getFirst().getProjectId());
    }

    @Test
    public void insertTask() {insertTestTask(generateRandomTask());}

    @Test
    public void insertWithoutDescription() {
        final TaskAddRequest request = new TaskAddRequest(
                1L,
                null,
                "Task Name " + System.currentTimeMillis(),
                null
        );
        final ResponseEntity<Long> taskResponse = restTemplate.postForEntity(
                "/task",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, taskResponse.getStatusCode());
        final Long id = taskResponse.getBody();
        Assertions.assertNotNull(id, "Task ID should not be null");
        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody(), "Task should not be null");
        Assertions.assertEquals(id, getResponse.getBody().getId(), "Task ID should match");
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName(), "Task name should match");
        Assertions.assertNull(getResponse.getBody().getDescription(), "Task description should be null");
    }

    @Test
    public void deleteTask(){
        final TaskAddRequest request = generateRandomTask();
        final long taskId = insertTestTask(request);

        final ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/task/" + taskId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        final ResponseEntity<ResourceNotFoundException> getResponse = restTemplate.getForEntity(
                "/task/" + taskId,
                ResourceNotFoundException.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(), "Task should be deleted");
    }

    @Test
    public void updateTask() {
        final TaskAddRequest request = generateRandomTask();
        final long taskId = insertTestTask(request);

        request.setName("Updated Task Name");
        request.setDescription("Updated Task Description");

        final TaskEditRequest editRequest = new TaskEditRequest(
                request.getName(),
                request.getDescription(),
                TaskStatus.DONE.toString()
        );
        final ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "/task/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + taskId,
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody(), "Task should not be null");
        Assertions.assertEquals(taskId, getResponse.getBody().getId(), "Task ID should match");
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName(), "Task name should match");
        Assertions.assertEquals(request.getDescription(), getResponse.getBody().getDescription(), "Task description should match");
        Assertions.assertEquals(TaskStatus.DONE, getResponse.getBody().getStatus(), "Task status should be DONE");
    }

    @Test
    public void changeStatus() {
        final TaskAddRequest request = generateRandomTask();
        final long taskId = insertTestTask(request);

        final TaskChangeStatusRequest editRequest = new TaskChangeStatusRequest(TaskStatus.IN_PROGRESS);
        final ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "/task/" + taskId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + taskId,
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody(), "Task should not be null");
        Assertions.assertEquals(taskId, getResponse.getBody().getId(), "Task ID should match");
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName(), "Task name should match");
        Assertions.assertEquals(request.getDescription(), getResponse.getBody().getDescription(), "Task description should match");
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, getResponse.getBody().getStatus(), "Task status should be IN_PROGRESS");
    }

    @Test
    public void assignTask(){
        final TaskAddRequest request = generateRandomTask();
        final long taskId = insertTestTask(request);

        final TaskAssignStatusRequest assignRequest = new TaskAssignStatusRequest(1L);
        final ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "/task/" + taskId + "/assign",
                HttpMethod.PUT,
                new HttpEntity<>(assignRequest),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + taskId,
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody(), "Task should not be null");
        Assertions.assertEquals(taskId, getResponse.getBody().getId(), "Task ID should match");
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName(), "Task name should match");
        Assertions.assertEquals(request.getDescription(), getResponse.getBody().getDescription(), "Task description should match");

    }

    // utility method to generate a random task request
    private TaskAddRequest generateRandomTask() {
        return new TaskAddRequest(
                1L, 
                2L,
                "Task Name " + System.currentTimeMillis(),
                "Task Description " + System.currentTimeMillis()
        );
    }

    private long insertTestTask(TaskAddRequest request) {
        final ResponseEntity<Long> taskResponse = restTemplate.postForEntity(
                "/task",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, taskResponse.getStatusCode());
        final Long id = taskResponse.getBody();
        Assertions.assertNotNull(id, "Task ID should not be null");
        final ResponseEntity<Task> getResponse = restTemplate.getForEntity(
                "/task/" + id,
                Task.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody(), "Task should not be null");
        Assertions.assertEquals(id, getResponse.getBody().getId(), "Task ID should match");
        Assertions.assertEquals(TaskStatus.NEW, getResponse.getBody().getStatus(), "Task status should be NEW");
        return id;
    }
}