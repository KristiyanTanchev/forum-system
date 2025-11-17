package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.TempAuthenticationHelper;
import com.team3.forum.models.Comment;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
import com.team3.forum.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentRestController {
    private final CommentService commentService;
    private final TempAuthenticationHelper authenticationHelper;

    @Autowired
    public CommentRestController(CommentService commentService,
                                 TempAuthenticationHelper authenticationHelper) {
        this.commentService = commentService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable int postId) {
        List<Comment> comments = commentService.findAllByPostId(postId);
        List<CommentResponseDto> response = comments.stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .postId(comment.getPost().getId())
                        .userId(comment.getUser().getId())
                        .content(comment.getContent())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestHeader HttpHeaders headers,
            @PathVariable int postId,
            @Valid @RequestBody CommentCreationDto commentCreationDto) {
        User user = authenticationHelper.tryGetUser(headers);
        Comment comment = commentService.createComment(commentCreationDto, postId, user.getId());

        CommentResponseDto response = CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @RequestHeader HttpHeaders headers,
            @PathVariable int postId,
            @PathVariable int commentId,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        User user = authenticationHelper.tryGetUser(headers);
        Comment updated = commentService.updateComment(commentId, commentUpdateDto, user.getId());

        CommentResponseDto response = CommentResponseDto.builder()
                .id(updated.getId())
                .postId(updated.getPost().getId())
                .userId(updated.getUser().getId())
                .content(updated.getContent())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @RequestHeader HttpHeaders headers,
            @PathVariable int postId,
            @PathVariable int commentId) {
        User user = authenticationHelper.tryGetUser(headers);
        commentService.deleteById(commentId, user.getId());
        return ResponseEntity.noContent().build();
    }
}