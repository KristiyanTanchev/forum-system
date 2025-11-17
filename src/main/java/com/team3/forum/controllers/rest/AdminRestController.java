package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserResponseDto;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AdminRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUser(query);
        List<UserResponseDto> response = users.stream().map(userMapper::toResponseDto).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/block")
    public ResponseEntity<UserResponseDto> blockUser(@PathVariable int id) {
        User user = userService.blockUser(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping("/users/{id}/unblock")
    public ResponseEntity<UserResponseDto> unblockUser(@PathVariable int id) {
        User user = userService.unblockUser(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @PostMapping("/users/{id}/promote")
    public ResponseEntity<UserResponseDto> promoteUser(@PathVariable int id) {
        User user = userService.promoteToAdmin(id);
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable int id) {
        userService.softDeleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/restore")
    public ResponseEntity<Void> restoreUser(@PathVariable int id) {
        userService.restoreById(id);
        return ResponseEntity.noContent().build();
    }
    //ToDo Admin must be able to delete any post
    //ToDo Admin must be able to filter and sort posts
}
