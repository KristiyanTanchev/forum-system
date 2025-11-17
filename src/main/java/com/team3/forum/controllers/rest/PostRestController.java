package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.TempAuthenticationHelper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.likeDtos.LikeCountDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.postDtos.PostUpdateDto;
import com.team3.forum.services.PostService;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    @Autowired
    public PostRestController(PostService postService,
                              PostMapper postMapper,
                              UserService userService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAll() {
        List<PostResponseDto> response = postService.findAll().stream()
                .map(postMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> create(
            @RequestBody @Valid PostCreationDto postCreationDto,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);
        Post post = postMapper.toEntity(postCreationDto, requester);
        Post detached = postService.create(post);
        PostResponseDto response = postMapper.toResponseDto(detached);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable int postId) {
        Post detached = postService.findById(postId);
        PostResponseDto response = postMapper.toResponseDto(detached);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @RequestBody @Valid PostUpdateDto postUpdateDto,
            @PathVariable int postId,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);
        Post detached = postService.update(postId, postUpdateDto, requester);
        PostResponseDto response = postMapper.toResponseDto(detached);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable int postId,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);
        postService.deleteById(postId, requester);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<LikeCountDto> getLikes(@PathVariable int postId) {
        int likes = postService.getLikes(postId);
        LikeCountDto response = new LikeCountDto(postId, likes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<LikeCountDto> likePost(
            @PathVariable int postId,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);
        postService.likePost(postId, requester.getId());

        int likes = postService.getLikes(postId);
        LikeCountDto response = new LikeCountDto(postId, likes);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<LikeCountDto> unlikePost(
            @PathVariable int postId,
            Authentication authentication) {
        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);
        postService.unlikePost(postId, requester.getId());

        int likes = postService.getLikes(postId);
        LikeCountDto response = new LikeCountDto(postId, likes);
        return ResponseEntity.ok(response);
    }

}
