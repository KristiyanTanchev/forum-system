package com.team3.forum.models.userDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {
    @NotBlank(message = "First name is required")
    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 characters")
    private String lastName;
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
