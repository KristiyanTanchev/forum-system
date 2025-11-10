package com.team3.forum.services;

import com.team3.forum.models.User;

import java.util.List;

public interface UserService {
    User save(User entity);
    User findById(int id);
    boolean existsById(int id);
    List<User> findAll();
    void deleteById(int id);
    void delete(User entity);
}
