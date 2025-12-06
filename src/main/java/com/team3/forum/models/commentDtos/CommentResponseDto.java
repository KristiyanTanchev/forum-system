package com.team3.forum.models.commentDtos;

import com.team3.forum.models.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private int id;
    private int postId;
    private int userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private LocalDateTime deletedAt;
    private int likesCount;
    private boolean likedByCurrentUser;
    private String createdAtString;
    private String editedAtString;
    private User user;
    private String username;
    private Set<User> likedBy;
}