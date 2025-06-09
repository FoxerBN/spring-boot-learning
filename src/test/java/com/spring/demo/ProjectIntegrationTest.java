package com.spring.demo;

import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.ProjectAddRequest;
import com.spring.demo.api.request.ProjectEditRequest;
import com.spring.demo.domain.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ProjectIntegrationTest extends IntegrationTest {
    @Test
    public void getAllProjects() {
        final ResponseEntity<List<Project>> projectResponse = restTemplate.exchange(
                "/project",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Assertions.assertEquals(HttpStatus.OK, projectResponse.getStatusCode());
        Assertions.assertNotNull(projectResponse.getBody());
        Assertions.assertFalse(projectResponse.getBody().isEmpty());
    }

    @Test
    public void getProjectByUserId() {
        final ResponseEntity<List<Project>> projectResponse = restTemplate.exchange(
                "/project?userId=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, projectResponse.getStatusCode());
        Assertions.assertNotNull(projectResponse.getBody());
        Assertions.assertFalse(projectResponse.getBody().isEmpty());
    }

    @Test
    public void insertProject() {
        insertTestProject(generateRandomProject());
    }

    @Test
    public void insertProjectWithoutDescription(){
        final ProjectAddRequest request = new ProjectAddRequest(1L,"name" + System.currentTimeMillis(), null);
        final ResponseEntity<Long> projectResponse = restTemplate.postForEntity(
                "/project",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        final Long id = projectResponse.getBody();
        Assertions.assertNotNull(id);

        final ResponseEntity<Project> getResponse = restTemplate.getForEntity(
                "/project/" + id,
                Project.class
        );
        Assertions.assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());
        Assertions.assertEquals(id, getResponse.getBody().getId());
        Assertions.assertEquals(request.getName(), getResponse.getBody().getName());
        Assertions.assertNull(getResponse.getBody().getDescription(), "Description should be null");
    }

    @Test
    public void getProjectById(){
        final ProjectAddRequest request = generateRandomProject();
        final long projectId = insertTestProject(request);

        final ResponseEntity<Project> projectResponse = restTemplate.getForEntity(
                "/project/" + projectId,
                Project.class
        );
        Assertions.assertEquals(HttpStatus.OK, projectResponse.getStatusCode());
        Assertions.assertNotNull(projectResponse.getBody());
        Assertions.assertEquals(projectId, projectResponse.getBody().getId());
        Assertions.assertEquals(request.getName(), projectResponse.getBody().getName());
        Assertions.assertEquals(request.getDescription(), projectResponse.getBody().getDescription());
    }

    @Test
    public void deleteProject(){
        final ProjectAddRequest request = generateRandomProject();
        final long projectId = insertTestProject(request);

        final ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/project/" + projectId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        Assertions.assertNull(deleteResponse.getBody(), "Response body should be null");
        final ResponseEntity<ResourceNotFoundException> getResponse = restTemplate.getForEntity(
                "/project/" + projectId,
                ResourceNotFoundException.class
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void updateProject(){
        final ProjectAddRequest request = generateRandomProject();
        final long projectId = insertTestProject(request);

        final ProjectEditRequest editRequest = new ProjectEditRequest("editedName", "editedDescription");
        final ResponseEntity<Void> updateResponse = restTemplate.exchange(
                "/project/" + projectId,
                HttpMethod.PUT,
                new HttpEntity<>(editRequest),
                Void.class
        );
        Assertions.assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

        // get updated project and compare data
        final ResponseEntity<Project> projectResponse = restTemplate.getForEntity(
                "/project/" + projectId,
                Project.class
        );
        Assertions.assertEquals(HttpStatus.OK, projectResponse.getStatusCode());
        Assertions.assertNotNull(projectResponse.getBody());
        Assertions.assertEquals(projectId, projectResponse.getBody().getId());
        Assertions.assertEquals(editRequest.getName(), projectResponse.getBody().getName());
        Assertions.assertEquals(editRequest.getDescription(), projectResponse.getBody().getDescription());
    }
    // UTILS
    private ProjectAddRequest generateRandomProject() {
        return new ProjectAddRequest(
                1L,
                "Project " + java.util.UUID.randomUUID(),
                "Description for project " + System.currentTimeMillis()
        );
    }

    private long insertTestProject(ProjectAddRequest request) {
        final ResponseEntity<Long> projectResponse = restTemplate.postForEntity(
                "/project",
                request,
                Long.class
        );
        Assertions.assertEquals(HttpStatus.CREATED, projectResponse.getStatusCode());
        Assertions.assertNotNull(projectResponse.getBody());
        Assertions.assertTrue(projectResponse.getBody() > 0, "Project ID should be greater than 0");

        final ResponseEntity<String> duplicateResponse = restTemplate.postForEntity(
                "/project",
                request,
                String.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, duplicateResponse.getStatusCode());

        return projectResponse.getBody();
    }

}
