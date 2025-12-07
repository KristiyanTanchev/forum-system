package com.team3.forum.controllers.rest;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.likeDtos.LikeCountDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostPage;
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
    private final UserService userService;

    @Autowired
    public PostRestController(PostService postService,
                              UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAll() {
        List<PostResponseDto> response = postService.findAll().stream()
                .map(postService::buildPostResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<PostPage> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "date") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int tagId
    ) {
        PostPage response = postService.getPostsInFolderPaginated(
                null,
                1,
                searchQuery,
                orderBy,
                direction,
                tagId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> create(
            @RequestBody @Valid PostCreationDto postCreationDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post detached = postService.create(postCreationDto, userDetails.getId());
        PostResponseDto response = postService.buildPostResponseDto(detached);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable int postId) {
        Post detached = postService.findById(postId);
        PostResponseDto response = postService.buildPostResponseDto(detached);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @RequestBody @Valid PostUpdateDto postUpdateDto,
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Post detached = postService.update(postId, postUpdateDto, principal.getId());
        PostResponseDto response = postService.buildPostResponseDto(detached);
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
        PostResponseDto response = postService.buildPostResponseDto(detached);
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

    @GetMapping("/user")
    public ResponseEntity<List<PostResponseDto>> getOwnPosts(
            @RequestParam int userId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            throw new AuthorizationException("You must be logged in to view user's posts!");
        }
        if (principal.getId() != userId && !principal.isModerator()) {
            throw new AuthorizationException("You are not allowed to view other users' posts!");
        }
        User user = userService.findById(userId);
        List<PostResponseDto> response = user.getPosts().stream().map(postService::buildPostResponseDto).toList();
        return ResponseEntity.ok(response);
    }
}
