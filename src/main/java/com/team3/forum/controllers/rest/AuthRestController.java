package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.loginDtos.LoginRequestDto;
import com.team3.forum.models.loginDtos.LoginResponseDto;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserResponseDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.security.JwtTokenProvider;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthRestController(AuthenticationManager authenticationManager, UserService userService, UserMapper userMapper, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserCreateDto userCreateDto) {
        User user = userService.createUser(userCreateDto);
        UserResponseDto response = userMapper.toResponseDto(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LoginResponseDto response = LoginResponseDto.builder()
                .token(jwt)
                .type("Bearer")
                .userId(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .isAdmin(userDetails.isAdmin())
                .build();

        return ResponseEntity.ok(response);
    }
}


