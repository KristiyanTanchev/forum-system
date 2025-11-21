package com.team3.forum.controllers.rest;

import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.User;
import com.team3.forum.models.folderDtos.FolderContentsDto;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.folderDtos.FolderUpdateDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.services.FolderService;
import com.team3.forum.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class FolderRestController {
    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final UserService userService;
    private final PostMapper postMapper;

    public FolderRestController(FolderService folderService, FolderMapper folderMapper, UserService userService, PostMapper postMapper) {
        this.folderService = folderService;
        this.folderMapper = folderMapper;
        this.userService = userService;
        this.postMapper = postMapper;
    }

    @GetMapping
    public ResponseEntity<List<FolderResponseDto>> getHome() {
        List<FolderResponseDto> response = folderService.findHomeFolders().stream()
                .map(folderMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FolderResponseDto> create(
            @RequestBody @Valid FolderCreateDto folderCreateDto,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);

        Folder folder = folderMapper.toEntity(folderCreateDto);

        Folder detached = folderService.create(folder, requester);
        FolderResponseDto response = folderMapper.toResponseDto(detached);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{*path}")
    public ResponseEntity<FolderContentsDto> getFolderContents(
            @PathVariable("path") String path) {

        List<String> slugs = List.of(path.substring(1).split("/"));

        Folder folder = folderService.getFolderByPath(slugs);

        FolderResponseDto folderDto = folderMapper.toResponseDto(folder);

        List<FolderResponseDto> subFolders = folder.getChildFolders().stream()
                .map(folderMapper::toResponseDto)
                .toList();

        List<PostResponseDto> posts = folderService.getPostsInFolder(folder).stream()
                .map(postMapper::toResponseDto)
                .toList();

        FolderContentsDto response = new FolderContentsDto(folderDto, subFolders, posts);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{*path}")
    public ResponseEntity<FolderResponseDto> createChild(
            @PathVariable("path") String path,
            @RequestBody @Valid FolderCreateDto folderCreateDto,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);

        List<String> slugs = List.of(path.substring(1).split("/"));
        Folder parent = folderService.getFolderByPath(slugs);

        Folder folder = folderMapper.toEntity(folderCreateDto);
        folder.setParentFolder(parent);

        Folder created = folderService.create(folder, requester);
        FolderResponseDto response = folderMapper.toResponseDto(created);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{*path}")
    public ResponseEntity<FolderResponseDto> updateFolder(
            @PathVariable("path") String path,
            @RequestBody @Valid FolderUpdateDto folderUpdateDto,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);

        List<String> slugs = List.of(path.substring(1).split("/"));
        Folder folder = folderService.getFolderByPath(slugs);

        Folder updated = folderService.update(folder, folderUpdateDto, requester);

        FolderResponseDto response = folderMapper.toResponseDto(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{*path}")
    public ResponseEntity<Void> deleteFolder(
            @PathVariable("path") String path,
            Authentication authentication) {

        String currentUsername = authentication.getName();
        User requester = userService.findByUsername(currentUsername);

        List<String> slugs = List.of(path.substring(1).split("/"));
        Folder folder = folderService.getFolderByPath(slugs);

        folderService.deleteById(folder.getId(), requester);
        return ResponseEntity.noContent().build();
    }
}
