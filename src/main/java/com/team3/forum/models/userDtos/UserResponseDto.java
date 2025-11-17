package com.team3.forum.models.userDtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String avatarUrl;
    private boolean isAdmin;
    private LocalDateTime createdAt;
}
