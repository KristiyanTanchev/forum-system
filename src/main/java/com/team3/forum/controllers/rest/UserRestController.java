package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.userDtos.UserResponseDto;
import com.team3.forum.models.userDtos.UserSummaryDto;
import com.team3.forum.models.userDtos.UserUpdateDto;
import com.team3.forum.security.CustomUserDetails;
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

    @Autowired
    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @PostMapping("/{id}")
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
}
