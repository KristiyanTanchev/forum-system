package com.team3.forum.models.userDtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class UserResponseDto {
    @EqualsAndHashCode.Include
    private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;
    private LocalDateTime createdAt;
}
