package com.spring.demo.controller;

import com.spring.demo.api.TaskService;
import com.spring.demo.api.request.TaskAddRequest;
import com.spring.demo.api.request.TaskAssignStatusRequest;
import com.spring.demo.api.request.TaskChangeStatusRequest;
import com.spring.demo.api.request.TaskEditRequest;
import com.spring.demo.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long projectId) {
        if (userId != null) {
            return ResponseEntity.ok().body(taskService.getAllByUserId(userId));
        } else if (projectId != null) {
            return ResponseEntity.ok().body(taskService.getAllByProjectId(projectId));
        } else {
            return ResponseEntity.ok().body(taskService.getAll());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(taskService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> add(@RequestBody TaskAddRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.add(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> edit(@PathVariable("id") long id, @RequestBody TaskEditRequest request) {
        taskService.edit(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable("id") long id, @RequestBody TaskChangeStatusRequest request) {
        taskService.changeStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @PutMapping("{id}/assign")
    public ResponseEntity<Void> changeStatus(@PathVariable("id") long id, @RequestBody TaskAssignStatusRequest request) {
        taskService.assignProject(id, request.getProjectId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> changeStatus(@PathVariable("id") long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

}