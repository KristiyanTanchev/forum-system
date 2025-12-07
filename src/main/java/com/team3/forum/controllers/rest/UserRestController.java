package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.userDtos.UserResponseDto;
import com.team3.forum.models.userDtos.UserSummaryDto;
import com.team3.forum.models.userDtos.UserUpdateDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.CommentService;
import com.team3.forum.services.PostService;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public UserRestController(UserService userService, UserMapper userMapper, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.postService = postService;
        this.commentService = commentService;
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable int id,
                                                      @Valid @RequestBody UserUpdateDto userUpdateDto,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        User updatedUser = userService.updateUser(id, userUpdateDto,userDetails.getId());
        UserResponseDto userResponseDto = userMapper.toResponseDto(updatedUser);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable int id) {
        User user = userService.findById(id);
        UserResponseDto userResponseDto = userMapper.toResponseDto(user);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserSummaryDto> response = users.stream().map(userMapper::toSummaryDto).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        UserResponseDto userResponseDto = userMapper.toResponseDto(user);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostResponseDto>> getUserPosts(@PathVariable int id) {
        User user = userService.findById(id);
        List<PostResponseDto> response = user.getPosts().stream()
                .map(postService::buildPostResponseDto).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponseDto>> getUserComments(@PathVariable int id) {
        User user = userService.findById(id);
        List<CommentResponseDto> response = user.getComments().stream()
                .map(commentService::buildCommentResponseDto).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<List<PostResponseDto>> getCurrentUserPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        List<PostResponseDto> response = user.getPosts().stream()
                .map(postService::buildPostResponseDto).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/comments")
    public ResponseEntity<List<CommentResponseDto>> getCurrentUserComments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        List<CommentResponseDto> response = user.getComments().stream()
                .map(commentService::buildCommentResponseDto).toList();
        return ResponseEntity.ok(response);
    }
}
