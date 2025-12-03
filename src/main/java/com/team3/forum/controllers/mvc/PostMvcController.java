package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.Tag;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostPage;
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
            @RequestParam(defaultValue = "created_at") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int tagId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        PostPage pageInfo = postService.getPostsInFolderPaginated(null, page, orderBy, direction, tagId);
        model.addAttribute("pageInfo", pageInfo);
        List<PostResponseDto> posts = pageInfo.getItems().stream()
                .map(postMapper::toResponseDto).toList();

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

        model.addAttribute("direction", direction);

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
            @RequestParam(defaultValue = "0") int folderId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to create a post!";
        }
        Folder folder = null;
        if (folderId == 0) {
            folder = folderService.findHomeFolders().get(0);
        } else {
            folder = folderService.findById(folderId);
        }

        if (folder.getParentFolder() != null) {
            FolderResponseDto parentFolderDto = folderMapper.toResponseDto(folder.getParentFolder());
            model.addAttribute("parent", parentFolderDto);
        }
        if (folder.getParentFolder() == null) {
            model.addAttribute("parent", null);
        }
        List<Folder> siblingFolders = folderService.getSiblingFolders(folder);
        List<FolderResponseDto> siblingFolderResponseDtos = siblingFolders.stream()
                .map(folderMapper::toResponseDto).toList();

        model.addAttribute("siblingFolders", siblingFolderResponseDtos);

        model.addAttribute("folderName", folder.getName());

        model.addAttribute("folder", folderMapper.toResponseDto(folder));

        List<FolderResponseDto> childFolderResponseDtos = folder.getChildFolders().stream()
                .sorted((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()))
                .map(folderMapper::toResponseDto).toList();

        model.addAttribute("childFolders", childFolderResponseDtos);
        model.addAttribute("folder", folderMapper.toResponseDto(folder));
        PostCreationDto postCreationDto = new PostCreationDto();
        postCreationDto.setFolderId(folder.getId());
        model.addAttribute("post", postCreationDto);
        return "CreatePostView";
    }

    @PostMapping("/new")
    public String createPost(
            Model model,
            @Valid @ModelAttribute("post") PostCreationDto postCreationDto,
            BindingResult errors,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (errors.hasErrors()) {
            model.addAttribute("folder", folderMapper.toPathDto(folderService.findById(postCreationDto.getFolderId())));
            return "CreatePostView";
        }
        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to create a post!";
        }
        Post post = postMapper.toEntity(postCreationDto, principal.getId());
        post = postService.create(post);
        return "redirect:/forum/posts/" + post.getId();
    }
}
