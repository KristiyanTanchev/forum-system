package com.team3.forum.helpers;

import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserResponseDto;
import com.team3.forum.models.userDtos.UserSummaryDto;
import com.team3.forum.models.userDtos.UserUpdateDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .isAdmin(user.isAdmin())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toEntity(UserCreateDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    public void updateEntityFromDto(UserUpdateDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }

    public UserSummaryDto toSummaryDto(User user){
        return UserSummaryDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
