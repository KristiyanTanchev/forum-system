package helpers;

import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserUpdateDto;

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

    public static UserCreateDto createMockUserCreateDto() {
        UserCreateDto dto = new UserCreateDto();
        dto.setUsername("new_user");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setEmail("new.user@example.com");
        dto.setPassword("password123");
        return dto;
    }

    public static UserUpdateDto createMockUserUpdateDto() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setEmail("updated@example.com");
        dto.setAvatarUrl("https://example.com/avatar.jpg");
        return dto;
    }

}
