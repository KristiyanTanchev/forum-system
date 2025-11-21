package com.team3.forum.models.loginDtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Integer userId;
    private String username;
    private String email;
    private boolean isAdmin;
}