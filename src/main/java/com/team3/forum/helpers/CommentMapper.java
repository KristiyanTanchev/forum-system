package com.team3.forum.helpers;

import com.team3.forum.models.Comment;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.repositories.PostRepository;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MarkdownService markdownService;

    @Autowired
    public CommentMapper(PostRepository postRepository, UserRepository userRepository, MarkdownService markdownService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.markdownService = markdownService;
    }

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

    public CommentResponseDto toResponseDto(Comment comment, User currentUser) {
        String createdAtString = TimeAgo.toTimeAgo(comment.getCreatedAt());

        String editedAtString = null;
        LocalDateTime updatedAt = comment.getUpdatedAt();
        if (updatedAt != null && !comment.getCreatedAt().isEqual(updatedAt)) {
            editedAtString = "Edited · " + TimeAgo.toTimeAgo(updatedAt);
        }

        boolean likedByCurrentUser = false;
        if (currentUser != null) {
            likedByCurrentUser = comment.getLikedBy().stream()
                    .anyMatch(user -> user.getId() == currentUser.getId());
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .contentHtml(markdownService.toHtml(comment.getContent()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.isDeleted())
                .deletedAt(comment.getDeletedAt())
                .likesCount(comment.getLikedBy().size())
                .likedByCurrentUser(likedByCurrentUser)
                .createdAtString(createdAtString)
                .editedAtString(editedAtString)
                .user(comment.getUser())
                .username(comment.getUser().getUsername())
                .likedBy(comment.getLikedBy())
                .build();
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