package com.team3.forum.repositories;

import com.team3.forum.models.User;

import java.util.List;

public interface UserRepository {
    User save(User entity);

    User findById(int id);

    boolean existsById(int id);

    List<User> findAll();

    void softDeleteById(int id);

    void restoreById(int id);

    User findByUsername(String username);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    boolean existsByUsername(String username);

    List<User> searchUsers(String searchTerm);

    int getUsersCount();
}
