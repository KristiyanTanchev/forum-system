package com.team3.forum.helpers;

import com.team3.forum.models.Comment;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentCreationDto dto, int postId, int userId) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());

        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);

        User user = new User();
        user.setId(userId);
        comment.setUser(user);

        return comment;
    }

    public CommentResponseDto convertToDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.isDeleted())
                .deletedAt(comment.getDeletedAt())
                .likesCount(comment.getLikedBy().size())
                .build();
    }
}