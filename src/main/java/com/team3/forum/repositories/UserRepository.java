package com.team3.forum.repositories;

import com.team3.forum.models.User;

import java.util.List;

public interface UserRepository {
    User save(User entity);
    User findById(int id);
    boolean existsById(int id);
    List<User> findAll();
    void deleteById(int id);
    void delete(User entity);
}
