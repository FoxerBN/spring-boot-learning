package com.spring.demo.implementation.jdbc.repository;

import com.spring.demo.api.exception.BadRequestException;
import com.spring.demo.api.exception.InternalErrorException;
import com.spring.demo.api.exception.ResourceNotFoundException;
import com.spring.demo.api.request.UserAddRequest;
import com.spring.demo.domain.User;
import com.spring.demo.implementation.jdbc.mapper.UserRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
public class UserJdbcRepository {

    private final UserRowMapper userRowMapper;
    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ALL = "SELECT * FROM user";
    private static final String GET_BY_ID = "SELECT * FROM user WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM user WHERE id = ?";
    private static final String INSERT_USER = "INSERT INTO user (id, name, email) VALUES (next value for user_id_seq, ?, ?)";

    public UserJdbcRepository(UserRowMapper userRowMapper, JdbcTemplate jdbcTemplate) {
        this.userRowMapper = userRowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }


    public long add(UserAddRequest request){
        try {
            final KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                final PreparedStatement stmt = connection.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, request.getName());
                stmt.setString(2, request.getEmail());
                return stmt;
            }, keyHolder);
            if(keyHolder.getKey() == null) {
                log.error("Failed to insert user: {}", request);
                throw new InternalErrorException("Failed to insert user");
            }
            return keyHolder.getKey().longValue();
        }catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while inserting user: {}", request, e);
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }catch (DataAccessException e){
            log.error("Error accessing data while inserting user: {}", request, e);
            throw new InternalErrorException("Error accessing data while inserting user");
        }
    }

    public void delete(long id){
        try {
            jdbcTemplate.update(DELETE_USER, id);
        } catch (DataAccessException e) {
            log.error("Error deleting user with id {}", id, e);
            throw new RuntimeException(e);
        }
    }

    public List<User> getAll() {
        return jdbcTemplate.query(GET_ALL, userRowMapper);
    }

    public User getById(long id) {
        try {
            return jdbcTemplate.queryForObject(GET_BY_ID, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("User with id {} not found", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        } catch (DataAccessException e) {
            log.error("Error accessing data for user with id {}", id, e);
            throw new InternalErrorException("Error accessing data for user with id: " + id);
        }
    }


}
