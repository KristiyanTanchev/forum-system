package com.team3.forum;

import com.team3.forum.models.User;

import java.time.LocalDateTime;

public class UserHelpers {
    public static User createMockUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encoded_password");
        user.setAdmin(false);
        user.setBlocked(false);
        user.setDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

}
