package com.team3.forum.controllers.rest;

import com.team3.forum.models.Tag;
import com.team3.forum.models.tagDtos.TagCreationDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.models.tagDtos.TagUpdateDto;
import com.team3.forum.services.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {

    private final TagService tagService;

    @Autowired
    public TagRestController(TagService tagService) {
        this.tagService = tagService;
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
    public ResponseEntity<TagResponseDto> createTag(@Valid @RequestBody TagCreationDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        Tag created = tagService.createTag(tag);
        TagResponseDto response = new TagResponseDto(created.getId(), created.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(
            @PathVariable int id,
            @Valid @RequestBody TagUpdateDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        Tag updated = tagService.updateTag(id, tag);
        TagResponseDto response = new TagResponseDto(updated.getId(), updated.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable int id) {
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}