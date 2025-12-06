package com.team3.forum.services;

import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserPage;
import com.team3.forum.models.userDtos.UserStatsDto;
import com.team3.forum.models.userDtos.UserUpdateDto;

import java.util.List;

public interface UserService {
    User createUser(UserCreateDto dto);

    User updateUser(int id, UserUpdateDto dto, int requesterId);

    User findById(int id);

    boolean existsById(int id);

    List<User> findAll();

    User findByUsername(String username);

    User blockUser(int id, int requesterId);

    User unblockUser(int id, int requesterId);

    User promoteToAdmin(int id);

    User promoteToModerator(int userId);

    public List<User> searchUsers(String query);

    void softDeleteById(int id);

    void restoreById(int id);

    UserStatsDto getUserStats(int userId);

    int getUsersCount();

    UserPage getUsersWithFiltersPaginated(int page, int size, String searchQuery, String statusFilter, String sortBy, String direction);

    int getBlockedUsersCount();
}
