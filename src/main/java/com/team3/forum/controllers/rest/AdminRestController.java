package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserPage;
import com.team3.forum.models.userDtos.UserResponseDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.CommentService;
import com.team3.forum.services.PostService;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public AdminRestController(UserService userService, UserMapper userMapper, PostService postService, CommentService commentService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getAdminStats(){
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getUsersCount());
        stats.put("totalPosts", postService.getPostsCount());
        stats.put("totalComments", commentService.getCommentCount());
        stats.put("blockedUsers", userService.getBlockedUsersCount());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<UserPage> getUsersWithFilters(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "username") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        UserPage userPage = userService.getUsersWithFiltersPaginated(page, size, search, status, sort, direction);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        List<UserResponseDto> response = users.stream().map(userMapper::toResponseDto).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<UserResponseDto> blockUser(@PathVariable int id,
                                                      @AuthenticationPrincipal CustomUserDetails requester) {
        User user = userService.blockUser(id, requester.getId());
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<UserResponseDto> unblockUser(@PathVariable int id,
                                                        @AuthenticationPrincipal CustomUserDetails requester) {
        User user = userService.unblockUser(id, requester.getId());
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping("/users/{id}/promote-admin")
    public ResponseEntity<UserResponseDto> promoteToAdmin(@PathVariable int id) {
        User user = userService.promoteToAdmin(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping("/users/{id}/promote-moderator")
    public ResponseEntity<UserResponseDto> promoteToModerator(@PathVariable int id) {
        User user = userService.promoteToModerator(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping("/users/{id}/demote-moderator")
    public ResponseEntity<UserResponseDto> demoteModerator(@PathVariable int id) {
        User user = userService.demoteUser(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable int id) {
        userService.softDeleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/restore")
    public ResponseEntity<Void> restoreUser(@PathVariable int id) {
        userService.restoreById(id);
        return ResponseEntity.noContent().build();
    }
}
