package com.spring.demo.implementation.jdbc.repository;

import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.exception.InternalErrorException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.ProjectAddRequest;
import com.spring.demo.api.request.ProjectEditRequest;
import com.spring.demo.domain.Project;
import com.spring.demo.implementation.jdbc.mapper.ProjectRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProjectJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProjectRowMapper projectRowMapper;
    private final UserJdbcRepository userRepository;

    private static final String GET_ALL = "SELECT * FROM project";
    private static final String GET_BY_ID = "SELECT * FROM project WHERE id = ?";
    private static final String GET_ALL_BY_USER = "SELECT * FROM project WHERE user_id = ?";

    private static final String INSERT_PROJECT = "INSERT INTO project (id, user_id, name, description, created_at)" +
                                                 " VALUES (next value for project_id_seq, ?, ?, ?, ?)";
    private static final String DELETE_PROJECT = "DELETE FROM project WHERE id = ?";
    private static final String UPDATE_PROJECT = "UPDATE project SET name = ?, description = ? WHERE id = ?";
    private static final String DELETE_ALL_BY_USER = "DELETE FROM project WHERE user_id = ?";

    public List<Project> getAll(){
        try {
            return jdbcTemplate.query(GET_ALL, projectRowMapper);
        }catch (DataAccessException e){
            log.error("Error accessing data while fetching all projects", e);
            throw new RuntimeException("Error accessing data while fetching all projects", e);
        }
    }

    public Project getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, projectRowMapper, id);
        }catch (EmptyResultDataAccessException e){
            log.warn("No project found with id: {}", id);
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        catch (DataAccessException e) {
            log.error("Error accessing data while fetching project by id: {}", id, e);
            throw new RuntimeException("Error accessing data while fetching project by id: " + id, e);
        }
    }

    public List<Project> getAllByUser(long userId){
        try {
            return jdbcTemplate.query(GET_ALL_BY_USER, projectRowMapper, userId);
        } catch (DataAccessException e) {
            log.error("Error accessing data while fetching all projects by user", e);
            throw new InternalErrorException("Error accessing data while fetching all projects by user");
        }
    }

    public long addProject(ProjectAddRequest request){
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection ->{
                final PreparedStatement stmt = connection.prepareStatement(INSERT_PROJECT, PreparedStatement.RETURN_GENERATED_KEYS);
                if (userRepository.getById(request.getUserId()) == null) {
                    log.error("User not found with id: {}", request.getUserId());
                    throw new ResourceNotFoundException("User not found with id: " + request.getUserId());
                }
                stmt.setLong(1, request.getUserId());
                stmt.setString(2, request.getName());
                if (request.getDescription() != null){
                    stmt.setString(3, request.getDescription());
                }else {
                    stmt.setNull(3, Types.VARCHAR);
                }
                stmt.setTimestamp(4, Timestamp.from(OffsetDateTime.now().toInstant()));
                return stmt;
            },keyHolder);
            if (keyHolder.getKey() == null){
                log.error("Failed to insert project: {}", request);
                throw new InternalErrorException("Failed to insert project");
            }
            return keyHolder.getKey().longValue();
        }catch (DataIntegrityViolationException e){
            log.error("Data integrity violation while inserting project: {}", request, e);
            throw new BadRequestException("Project with name " + request.getName() + " already exists");
        }catch (DataAccessException e){
            log.error("Error accessing data while inserting project: {}", request, e);
            throw new InternalErrorException("Error accessing data while inserting project");
        }
    }

    public void updateProject(long id, ProjectEditRequest request){
        try {
            jdbcTemplate.update(UPDATE_PROJECT, request.getName(), request.getDescription(),id);
        }catch (DataAccessException e){
            log.error("Error accessing data while updating project: {}", request, e);
            throw new InternalErrorException("Error accessing data while updating project");
        }
    }

    public void deleteProject(long id){
        try {
            jdbcTemplate.update(DELETE_PROJECT, id);
        }catch (EmptyResultDataAccessException e){
            log.warn("No project found with id: {}", id);
            throw new ResourceNotFoundException("Project not found with id: " + id);
        } catch (DataAccessException e) {
            log.error("Error accessing data while deleting project with id: {}", id, e);
            throw new InternalErrorException("Error accessing data while deleting project with id: " + id);
        }
    }

    public void deleteAllByUser(long userId){
        try {
            jdbcTemplate.update(
                    DELETE_ALL_BY_USER,
                    userId
            );
        }catch (DataAccessException e){
            log.error("Error accessing data while deleting all projects by user with id: {}", userId, e);
            throw new InternalErrorException("Error accessing data while deleting all projects by user with id: " + userId);
        }
    }
}
