package com.team3.forum.services;

import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserUpdateDto;

import java.util.List;

public interface UserService {
    User createUser(UserCreateDto dto);          // ✅ DTO for input
    User updateUser(int id, UserUpdateDto dto);
    User findById(int id);
    boolean existsById(int id);
    List<User> findAll();
    void deleteById(int id);
    void delete(User entity);
}
