package com.team3.forum.helpers;

import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.*;
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
                .role(user.getRole().name())
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
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
    }

    public UserSummaryDto toSummaryDto(User user) {
        return UserSummaryDto
                .builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    public UserStatsDto toStatsDto(User user) {
        int topicCount = user.getPosts().size();
        int replyCount = user.getComments().size();

        int likesCount = user.getPosts().stream()
                .mapToInt(post -> post.getLikedBy().size())
                .sum();

        return UserStatsDto.builder()
                .topicCount(topicCount)
                .replyCount(replyCount)
                .likesCount(likesCount)
                .build();
    }

    public UserUpdateDto toUpdateDto(User user) {
        return UserUpdateDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
