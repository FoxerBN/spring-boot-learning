package com.spring.demo.api;

import com.spring.demo.api.request.ProjectAddRequest;
import com.spring.demo.api.request.ProjectEditRequest;
import com.spring.demo.domain.Project;

import java.util.List;

public interface ProjectService {
    Project get(long id);
    List<Project> getAll();
    List<Project> getAllByUser(long userId);
    void delete(long id);

    long add(ProjectAddRequest request);
    void edit(long id, ProjectEditRequest request);
}
