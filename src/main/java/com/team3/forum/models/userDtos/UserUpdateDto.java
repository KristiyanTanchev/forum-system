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
public class UserUpdateDto {
    @NotBlank(message = "First name is required")
    @Size(min = 4, max = 32, message = "First name must be between 4 and 32 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 4, max = 32, message = "Last name must be between 4 and 32 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String avatarUrl;
}
