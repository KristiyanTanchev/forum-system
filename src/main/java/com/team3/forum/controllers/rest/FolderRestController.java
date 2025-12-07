package com.team3.forum.controllers.rest;

import com.team3.forum.models.Folder;
import com.team3.forum.models.folderDtos.FolderContentsDto;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.folderDtos.FolderUpdateDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.FolderService;
import com.team3.forum.services.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class FolderRestController {
    private final FolderService folderService;
    private final PostService postService;

    public FolderRestController(FolderService folderService, PostService postService) {
        this.folderService = folderService;
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<FolderResponseDto>> getHome() {
        List<FolderResponseDto> response = folderService.findHomeFolders().stream()
                .map(folderService::buildFolderResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FolderResponseDto> create(
            @RequestBody @Valid FolderCreateDto folderCreateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Folder detached = folderService.create(folderCreateDto, new ArrayList<>(), userDetails.getId());
        FolderResponseDto response = folderService.buildFolderResponseDto(detached);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/path/{*path}")
    public ResponseEntity<FolderContentsDto> getFolderContents(
            @PathVariable("path") String path) {

        List<String> slugs = List.of(path.substring(1).split("/"));

        Folder folder = folderService.getFolderByPath(slugs);

        FolderResponseDto folderDto = folderService.buildFolderResponseDto(folder);

        List<FolderResponseDto> subFolders = folder.getChildFolders().stream()
                .map(folderService::buildFolderResponseDto)
                .toList();

        List<PostResponseDto> posts = folderService.getPostsInFolder(folder).stream()
                .map(postService::buildPostResponseDto)
                .toList();

        FolderContentsDto response = new FolderContentsDto(folderDto, subFolders, posts);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/path/{*path}")
    public ResponseEntity<FolderResponseDto> createChild(
            @PathVariable("path") String path,
            @RequestBody @Valid FolderCreateDto folderCreateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<String> slugs = List.of(path.substring(1).split("/"));

        Folder created = folderService.create(folderCreateDto, slugs, userDetails.getId());
        FolderResponseDto response = folderService.buildFolderResponseDto(created);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/path/{*path}")
    public ResponseEntity<FolderResponseDto> updateFolder(
            @PathVariable("path") String path,
            @RequestBody @Valid FolderUpdateDto folderUpdateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<String> slugs = List.of(path.substring(1).split("/"));

        Folder updated = folderService.update(slugs, folderUpdateDto, userDetails.getId());

        FolderResponseDto response = folderService.buildFolderResponseDto(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/path/{*path}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable("path") String path,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<String> slugs = List.of(path.substring(1).split("/"));
        Folder folder = folderService.getFolderByPath(slugs);

        folderService.deleteById(folder.getId(), userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/path/posts/{*path}")
    public ResponseEntity<List<PostResponseDto>> getFolderPosts(
            @PathVariable("path") String path,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "CREATED_AT") String orderBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        List<String> slugs = List.of(path.substring(1).split("/"));

        Folder folder = folderService.getFolderByPath(slugs);

        List<PostResponseDto> posts = postService.getPostsInFolderPaginated(folder, page, orderBy, direction).stream()
                .map(postService::buildPostResponseDto)
                .toList();

        return ResponseEntity.ok(posts);
    }
}
