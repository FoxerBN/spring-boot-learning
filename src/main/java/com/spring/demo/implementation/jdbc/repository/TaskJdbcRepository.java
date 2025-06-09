package com.spring.demo.implementation.jdbc.repository;

import com.spring.demo.api.exception.InternalErrorException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.TaskAddRequest;
import com.spring.demo.api.request.TaskEditRequest;
import com.spring.demo.domain.Task;
import com.spring.demo.domain.TaskStatus;
import com.spring.demo.implementation.jdbc.mapper.TaskRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TaskJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TaskRowMapper taskMapper;
    private final UserJdbcRepository userRepository;

    private static final String GET_ALL = "SELECT * FROM task";
    private static final String GET_BY_ID = "SELECT * FROM task WHERE id = ?";
    private static final String GET_ALL_BY_USER = "SELECT * FROM task WHERE user_id = ?";
    private static final String GET_ALL_BY_PROJECT = "SELECT * FROM task WHERE project_id = ?";

    private static final String INSERT_TASK = "INSERT INTO task (id, user_id, project_id, name, description,status, created_at)" +
            " VALUES (next value for task_id_seq, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE task SET name = ?, description = ?, status = ? WHERE id = ?";
    private final static String UPDATE_STATUS = "UPDATE task SET status = ? WHERE id = ?";
    private final static String UPDATE_PROJECT = "UPDATE task SET project_id = ? WHERE id = ?";
    private final static String DELETE = "DELETE FROM task WHERE id = ?";
    private final static String DELETE_ALL_BY_PROJECT = "DELETE FROM task WHERE project_id = ?";
    private final static String DELETE_ALL_BY_USER = "DELETE FROM task WHERE user_id = ?";
    // GET METHODS
    public List<Task> getAll() {
        try {
            return jdbcTemplate.query(GET_ALL, taskMapper);
        } catch (DataAccessException e) {
            log.error("Error accessing data while fetching all tasks", e);
            throw new InternalErrorException("Error accessing data while fetching all tasks");
        }
    }

    public Task getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, taskMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No task found with id: {}", id);
            throw new ResourceNotFoundException("Task not found with id: " + id);
        } catch (DataAccessException e) {
            log.warn("Error accessing data while fetching task with id: {}", id, e);
            throw new InternalErrorException("Task not found with id: " + id);
        }
    }

    public List<Task> getAllByUserId(long userId) {
        try {
            return jdbcTemplate.query(GET_ALL_BY_USER, taskMapper,userId);
        } catch (DataAccessException e) {
            log.error("Error accessing data while fetching all tasks", e);
            throw new InternalErrorException("Error accessing data while fetching all tasks");
        }
    }

    public List<Task> getAllByProjectId(long userId) {
        try {
            return jdbcTemplate.query(GET_ALL_BY_PROJECT, taskMapper,userId);
        } catch (DataAccessException e) {
            log.error("Error accessing data while fetching all tasks", e);
            throw new InternalErrorException("Error accessing data while fetching all tasks");
        }
    }

    //ADD METHODS

    public long add(TaskAddRequest request) {
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                final PreparedStatement stmt = connection.prepareStatement(INSERT_TASK, PreparedStatement.RETURN_GENERATED_KEYS);
                if (userRepository.getById(request.getUserId()) == null) {
                    log.error("User not found with id: {}", request.getUserId());
                    throw new ResourceNotFoundException("User not found with id: " + request.getUserId());
                }
                stmt.setLong(1, request.getUserId());
                if (request.getProjectId() != null && request.getProjectId() > 0) {
                    stmt.setLong(2, request.getProjectId());
                } else {
                    stmt.setNull(2, java.sql.Types.BIGINT);
                }
                stmt.setString(3, request.getName());
                if (request.getDescription() != null) {
                    stmt.setString(4, request.getDescription());
                } else {
                    stmt.setNull(4, java.sql.Types.VARCHAR);
                }
                stmt.setString(5, TaskStatus.NEW.toString());
                stmt.setTimestamp(6, Timestamp.from(OffsetDateTime.now().toInstant()));
                return stmt;
            }, keyHolder);
            if (keyHolder.getKey() == null) {
                log.error("Failed to insert task: {}", request);
                throw new InternalErrorException("Failed to insert task");
            }
            return keyHolder.getKey().longValue();
        } catch (DataAccessException e) {
            log.error("Error accessing data while inserting task: {}", request, e);
            throw new InternalErrorException("Error accessing data while inserting task");
        }
    }

    public void update(long id, TaskEditRequest request) {
        try {
            jdbcTemplate.update(
                    UPDATE,
                    request.getName(),
                    request.getDescription(),
                    request.getStatus(),
                    id
            );
        }catch (DataAccessException e){
            log.error("Error accessing data while updating task with id: {}", id, e);
            throw new InternalErrorException("Error accessing data while updating task with id: " + id);
        }
    }

    public void updateStatus(long id, TaskStatus status) {
        try {
            jdbcTemplate.update(
                    UPDATE_STATUS,
                    status.toString(),
                    id
            );
        } catch (DataAccessException e) {
            log.error("Error accessing data while updating task status with id: {}", id, e);
            throw new InternalErrorException("Error accessing data while updating task status with id: " + id);
        }
    }

    public void updateProject(long id, Long projectId) {
        try {
            jdbcTemplate.update(
                    UPDATE_PROJECT,
                    projectId,
                    id
            );
        } catch (DataAccessException e) {
            log.error("Error accessing data while updating task project with id: {}", id, e);
            throw new InternalErrorException("Error accessing data while updating task project with id: " + id);
        }
    }

    public void delete(long id){
        try {
            jdbcTemplate.update(
                    DELETE,
                    id
            );
        } catch (DataAccessException e) {
            log.error("Error accessing data while deleting task with id: {}", id, e);
            throw new InternalErrorException("Error accessing data while deleting task with id: " + id);
        }
    }

    public void deleteAllByProject(long projectId) {
        try {
            jdbcTemplate.update(
                    DELETE_ALL_BY_PROJECT,
                    projectId
            );
        } catch (DataAccessException e) {
            log.error("Error accessing data while deleting all tasks by project with id: {}", projectId, e);
            throw new InternalErrorException("Error accessing data while deleting all tasks by project with id: " + projectId);
        }
    }

    public void deleteAllByUser(long userId) {
        try {
            jdbcTemplate.update(
                    DELETE_ALL_BY_USER,
                    userId
            );
        } catch (DataAccessException e) {
            log.error("Error accessing data while deleting all tasks by user with id: {}", userId, e);
            throw new InternalErrorException("Error accessing data while deleting all tasks by user with id: " + userId);
        }
    }
}
