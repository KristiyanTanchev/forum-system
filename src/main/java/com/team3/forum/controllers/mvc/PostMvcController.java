package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Post;
import com.team3.forum.models.Tag;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.FolderService;
import com.team3.forum.services.PostService;
import com.team3.forum.services.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/forum/posts")
public class PostMvcController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final TagService tagService;

    @Autowired
    public PostMvcController(PostService postService, PostMapper postMapper, FolderService folderService, FolderMapper folderMapper, TagService tagService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.folderService = folderService;
        this.folderMapper = folderMapper;
        this.tagService = tagService;
    }

    @GetMapping
    public String getAllPosts(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "date") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int tagId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<PostResponseDto> posts = postService.getPostsInFolderPaginated(null, page, orderBy, direction, tagId)
                .stream().map(postMapper::toResponseDto).toList();
        model.addAttribute("posts", posts);
        if (tagId != 0) {
            Tag tag = tagService.findById(tagId);
            model.addAttribute("tag",
                    TagResponseDto.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .build()
            );
        }

        model.addAttribute("tagId", tagId);

        model.addAttribute("orderBy", orderBy);

        model.addAttribute("tags",
                tagService.findAll().stream()
                        .sorted((t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()))
        );
        return "AllPostsView";
    }

    @GetMapping("/{postId}")
    public String getPostPage(
            Model model,
            @PathVariable int postId,
            @RequestParam(defaultValue = "date") String sortCommentsBy,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (sortCommentsBy.equals("likes")) {
            model.addAttribute("sortCommentsBy", "likes");
        } else {
            model.addAttribute("sortCommentsBy", "date");
        }
        Post post = postService.findById(postId);
        if (principal != null) {
            postService.registerView(postId, principal.getId());
        }
        model.addAttribute("tags",
                post.getTags().stream()
                        .map(tag -> TagResponseDto.builder().id(tag.getId()).name(tag.getName()).build())
                        .toList()
        );

        model.addAttribute("post", postMapper.toResponseDto(post));
        return "PostView";
    }

    @GetMapping("/new")
    public String createPostPage(
            Model model,
            @RequestParam int folderId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (folderId == 0) {
            return "ErrorView404";
        }
        model.addAttribute("folder", folderMapper.toPathDto(folderService.findById(folderId)));
        PostCreationDto postCreationDto = new PostCreationDto();
        postCreationDto.setFolderId(folderId);
        model.addAttribute("post", postCreationDto);
        return "CreatePostView";
    }

    @PostMapping("/new")
    public String createPost(
            @Valid @ModelAttribute("post") PostCreationDto postCreationDto,
            BindingResult errors,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (errors.hasErrors()) {
            return "CreatePostView";
        }
        if (principal == null) {
            return "redirect:/login?error=You must be logged in to create a post!";
        }
        Post post = postMapper.toEntity(postCreationDto, principal.getId());
        post = postService.create(post);
        return "redirect:/forum/posts/" + post.getId();
    }
}
