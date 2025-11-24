package com.team3.forum.controllers.rest;

import com.team3.forum.models.Comment;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
import com.team3.forum.models.likeDtos.LikeCountDto;
import com.team3.forum.services.CommentService;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentRestController {
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public CommentRestController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable int postId,
            @RequestParam(defaultValue = "created_at") String orderBy,
            @RequestParam(defaultValue = "desc") String direction) {

        List<Comment> comments;
        if ("created_at".equals(orderBy) && "desc".equals(direction)) {
            comments = commentService.findAllByPostId(postId);
        } else {
            comments = commentService.findAllByPostIdWithOrdering(postId, orderBy, direction);
        }

        List<CommentResponseDto> response = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .postId(comment.getPost().getId())
                        .userId(comment.getUser().getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .isDeleted(comment.isDeleted())
                        .deletedAt(comment.getDeletedAt())
                        .likesCount(comment.getLikedBy().size())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable int postId,
            @Valid @RequestBody CommentCreationDto commentCreationDto,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Comment comment = commentService.createComment(commentCreationDto, postId, user);

        CommentResponseDto response = CommentResponseDto.builder()
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

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Comment updated = commentService.updateComment(commentId, commentUpdateDto, user);

        CommentResponseDto response = CommentResponseDto.builder()
                .id(updated.getId())
                .postId(updated.getPost().getId())
                .userId(updated.getUser().getId())
                .content(updated.getContent())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .isDeleted(updated.isDeleted())
                .deletedAt(updated.getDeletedAt())
                .likesCount(updated.getLikedBy().size())
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/{commentId}/restore")
    public ResponseEntity<CommentResponseDto> restoreComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Comment restored = commentService.restoreById(commentId, user);

        CommentResponseDto response = CommentResponseDto.builder()
                .id(restored.getId())
                .postId(restored.getPost().getId())
                .userId(restored.getUser().getId())
                .content(restored.getContent())
                .createdAt(restored.getCreatedAt())
                .updatedAt(restored.getUpdatedAt())
                .isDeleted(restored.isDeleted())
                .deletedAt(restored.getDeletedAt())
                .likesCount(restored.getLikedBy().size())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<LikeCountDto> getLikes(@PathVariable int commentId) {
        int likes = commentService.getLikes(commentId);
        LikeCountDto response = new LikeCountDto(commentId, likes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<LikeCountDto> likeComment(
            @PathVariable int commentId,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        commentService.likeComment(commentId, user);
        int likes = commentService.getLikes(commentId);
        LikeCountDto response = new LikeCountDto(commentId, likes);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<LikeCountDto> unlikeComment(
            @PathVariable int commentId,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        commentService.unlikeComment(commentId, user);
        int likes = commentService.getLikes(commentId);
        LikeCountDto response = new LikeCountDto(commentId, likes);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        commentService.deleteById(commentId, user);
        return ResponseEntity.noContent().build();
    }
}