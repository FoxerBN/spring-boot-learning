package com.spring.demo.controller;

import com.spring.demo.api.ProjectService;
import com.spring.demo.api.request.ProjectAddRequest;
import com.spring.demo.api.request.ProjectEditRequest;
import com.spring.demo.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getAll(@RequestParam(required = false) Long userId){
        if(userId != null) {
            return ResponseEntity.ok().body(projectService.getAllByUser(userId));
        }
        return ResponseEntity.ok().body(projectService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Project> getById(@PathVariable("id") long id){
        return ResponseEntity.ok().body(projectService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> add(@RequestBody ProjectAddRequest request){
        return ResponseEntity.status(201).body(projectService.add(request));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> edit(@PathVariable("id") long id, @RequestBody ProjectEditRequest request){
        projectService.edit(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable ("id") long id) {
        projectService.delete(id);
        return ResponseEntity.ok().build();
    }
}
