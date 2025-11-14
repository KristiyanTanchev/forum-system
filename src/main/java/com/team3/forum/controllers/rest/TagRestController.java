package com.team3.forum.controllers.rest;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.helpers.TempAuthenticationHelper;
import com.team3.forum.models.Tag;
import com.team3.forum.models.User;
import com.team3.forum.models.tagDtos.TagCreationDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.models.tagDtos.TagUpdateDto;
import com.team3.forum.services.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {

    private final TagService tagService;
    private final TempAuthenticationHelper authenticationHelper;

    @Autowired
    public TagRestController(TagService tagService,
                             TempAuthenticationHelper authenticationHelper) {
        this.tagService = tagService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        List<TagResponseDto> response = tagService.findAll().stream()
                .map(tag -> new TagResponseDto(tag.getId(), tag.getName()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable int id) {
        Tag tag = tagService.findById(id);
        TagResponseDto response = new TagResponseDto(tag.getId(), tag.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(
            @RequestHeader HttpHeaders headers,
            @Valid @RequestBody TagCreationDto dto) {
        User requester = authenticationHelper.tryGetUser(headers);
        if (!requester.isAdmin()) {
            throw new AuthorizationException("Only administrators can create tags");
        }

        Tag tag = new Tag();
        tag.setName(dto.getName());
        Tag created = tagService.createTag(tag);
        TagResponseDto response = new TagResponseDto(created.getId(), created.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(
            @RequestHeader HttpHeaders headers,
            @PathVariable int id,
            @Valid @RequestBody TagUpdateDto dto) {
        User requester = authenticationHelper.tryGetUser(headers);
        if (!requester.isAdmin()) {
            throw new AuthorizationException("Only administrators can update tags");
        }

        Tag tag = new Tag();
        tag.setName(dto.getName());
        Tag updated = tagService.updateTag(id, tag);
        TagResponseDto response = new TagResponseDto(updated.getId(), updated.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @RequestHeader HttpHeaders headers,
            @PathVariable int id) {
        User requester = authenticationHelper.tryGetUser(headers);
        if (!requester.isAdmin()) {
            throw new AuthorizationException("Only administrators can delete tags");
        }

        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}