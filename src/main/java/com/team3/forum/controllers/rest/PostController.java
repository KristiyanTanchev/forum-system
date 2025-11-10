package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Post;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @Autowired
    public PostController(PostService postService, PostMapper postMapper) {
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
    public ResponseEntity<PostResponseDto> create(@RequestBody @Valid PostCreationDto postCreationDto) {
        Post post = postMapper.toEntity(postCreationDto);
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
    public ResponseEntity<PostResponseDto> updatePost(@RequestBody @Valid PostCreationDto postCreationDto,
                                                      @PathVariable int postId) {
        Post post = postMapper.toEntity(postCreationDto, postId);
        Post detached = postService.update(post);
        PostResponseDto response = postMapper.toResponseDto(detached);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable int postId) {
        postService.deleteById(postId);
        return ResponseEntity.noContent().build();
    }
}
