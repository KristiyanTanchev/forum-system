package com.team3.forum.services;

import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserUpdateDto;

import java.util.List;

public interface UserService {
    User createUser(UserCreateDto dto);
    User updateUser(int id, UserUpdateDto dto, String currentUsername);
    User findById(int id);
    boolean existsById(int id);
    List<User> findAll();
    User findByUsername(String username);
    User blockUser(int id);
    User unblockUser(int id);
    User promoteToAdmin(int id);
    public List<User> searchUsers(String query);
    void softDeleteById(int id);
    void restoreById(int id);
}
