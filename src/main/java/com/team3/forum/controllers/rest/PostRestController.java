package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Post;
import com.team3.forum.models.likeDtos.LikeCountDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.postDtos.PostUpdateDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.PostService;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private final PostService postService;
    private final PostMapper postMapper;

    @Autowired
    public PostRestController(PostService postService,
                              PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
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
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = postMapper.toEntity(postCreationDto, userDetails.getId());
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
            @AuthenticationPrincipal CustomUserDetails principal) {
        Post detached = postService.update(postId, postUpdateDto, principal.getId());
        PostResponseDto response = postMapper.toResponseDto(detached);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        postService.deleteById(postId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/restore")
    public ResponseEntity<PostResponseDto> restorePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Post detached = postService.restoreById(postId, principal.getId());
        PostResponseDto response = postMapper.toResponseDto(detached);
        return ResponseEntity.ok(response);
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
            @AuthenticationPrincipal CustomUserDetails principal) {
        postService.likePost(postId, principal.getId());

        int likes = postService.getLikes(postId);
        LikeCountDto response = new LikeCountDto(postId, likes);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<LikeCountDto> unlikePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        postService.unlikePost(postId, principal.getId());

        int likes = postService.getLikes(postId);
        LikeCountDto response = new LikeCountDto(postId, likes);
        return ResponseEntity.ok(response);
    }

}
