package com.team3.forum.controllers.mvc;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.*;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostPage;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.postDtos.PostUpdateDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/forum/posts")
public class PostMvcController {
    private final static int FOLDER_PAGE_SIZE = 5;


    private final PostService postService;
    private final PostMapper postMapper;
    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final TagService tagService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostMvcController(PostService postService, PostMapper postMapper,
                             FolderService folderService, FolderMapper folderMapper,
                             TagService tagService, CommentService commentService,
                             UserService userService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.folderService = folderService;
        this.folderMapper = folderMapper;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllPosts(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "created_at") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int tagId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        PostPage pageInfo = postService.getPostsInFolderPaginated(null, page, search, orderBy, direction, tagId);
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
            @RequestParam(required = false) Integer editCommentId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (sortCommentsBy.equals("likes")) {
            model.addAttribute("sortCommentsBy", "likes");
        } else {
            model.addAttribute("sortCommentsBy", "date");
        }
        Post post;
        if (principal != null) {
            post = postService.findByIdIncludeDeleted(postId, principal.getId());
            postService.registerView(postId, principal.getId());
        } else {
            post = postService.findById(postId);
        }

        if (principal != null && principal.isAdmin() || post.getUser().getId() == principal.getId()) {
            model.addAttribute("canEdit", true);
        }

        model.addAttribute("tags",
                post.getTags().stream()
                        .map(tag -> TagResponseDto.builder().id(tag.getId()).name(tag.getName()).build())
                        .toList()
        );

        model.addAttribute("post", postMapper.toResponseDto(post));
        User currentUser = principal != null ? userService.findById(principal.getId()) : null;

        List<Comment> comments;
        if ("likes".equals(sortCommentsBy)) {
            comments = commentService.findAllByPostIdWithOrdering(postId, "likes", "desc");
        } else {
            comments = commentService.findAllByPostId(postId);
        }

        comments.forEach(comment -> {
            comment.getLikedBy().size();
        });

        model.addAttribute("comments", comments);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("commentCreationDto", new CommentCreationDto());

        if (editCommentId != null) {
            model.addAttribute("editingCommentId", editCommentId);
        }

        return "PostView";
    }

    @GetMapping("/new")
    public String createPostPage(
            Model model,
            @RequestParam(defaultValue = "0") int folderId,
            @RequestParam(defaultValue = "1") int siblingPage,
            @RequestParam(defaultValue = "1") int childPage,
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

        // ---------- SIBLING FOLDERS ----------
        List<Folder> allSiblingFolders = folderService.getSiblingFolders(folder);
        int siblingTotal = allSiblingFolders.size();
        int siblingTotalPages = siblingTotal == 0 ? 1
                : (int) Math.ceil((double) siblingTotal / FOLDER_PAGE_SIZE);

        siblingPage = Math.max(1, Math.min(siblingPage, siblingTotalPages));
        int siblingFrom = (siblingPage - 1) * FOLDER_PAGE_SIZE;
        int siblingTo = Math.min(siblingFrom + FOLDER_PAGE_SIZE, siblingTotal);

        List<FolderResponseDto> siblingFolderResponseDtos = allSiblingFolders
                .subList(siblingFrom, siblingTo).stream()
                .map(folderMapper::toResponseDto)
                .toList();

        model.addAttribute("siblingFolders", siblingFolderResponseDtos);
        model.addAttribute("siblingPage", siblingPage);
        model.addAttribute("siblingTotalPages", siblingTotalPages);

        // ---------- CHILD FOLDERS ----------
        List<Folder> allChildFolders = folder.getChildFolders().stream()
                .sorted((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()))
                .toList();

        int childTotal = allChildFolders.size();
        int childTotalPages = childTotal == 0 ? 1
                : (int) Math.ceil((double) childTotal / FOLDER_PAGE_SIZE);

        childPage = Math.max(1, Math.min(childPage, childTotalPages));
        int childFrom = (childPage - 1) * FOLDER_PAGE_SIZE;
        int childTo = Math.min(childFrom + FOLDER_PAGE_SIZE, childTotal);

        List<FolderResponseDto> childFolderResponseDtos = allChildFolders
                .subList(childFrom, childTo).stream()
                .map(folderMapper::toResponseDto)
                .toList();

        model.addAttribute("childFolders", childFolderResponseDtos);
        model.addAttribute("childPage", childPage);
        model.addAttribute("childTotalPages", childTotalPages);


        if (folder.getParentFolder() != null) {
            FolderResponseDto parentFolderDto = folderMapper.toResponseDto(folder.getParentFolder());
            model.addAttribute("parent", parentFolderDto);
        }
        if (folder.getParentFolder() == null) {
            model.addAttribute("parent", null);
        }

        model.addAttribute("folderName", folder.getName());

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


    @GetMapping("/{postId}/edit")
    public String editPostPage(
            Model model,
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to edit a post!";
        }

        Post post = postService.findById(postId);

        if (!principal.isAdmin() && post.getUser().getId() != principal.getId()) {
            return "redirect:/forum/posts/" + postId + "?error=You are not allowed to edit this post.";
        }

        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());

        var tags = post.getTags().stream().toList();
        if (tags.size() > 0) dto.setTag1(tags.get(0).getName());
        if (tags.size() > 1) dto.setTag2(tags.get(1).getName());
        if (tags.size() > 2) dto.setTag3(tags.get(2).getName());

        Folder folder = post.getFolder();

        model.addAttribute("folder", folderMapper.toResponseDto(folder));
        model.addAttribute("post", dto);
        model.addAttribute("postId", postId);

        return "EditPostView";
    }

    @PostMapping("/{postId}/edit")
    public String updatePost(
            Model model,
            @PathVariable int postId,
            @Valid @ModelAttribute("post") PostUpdateDto postUpdateDto,
            BindingResult errors,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to edit a post!";
        }

        Post existing = postService.findById(postId);

        // Authorization: only owner or admin
        if (!principal.isAdmin() && existing.getUser().getId() != principal.getId()) {
            return "redirect:/forum/posts/" + postId + "?error=You are not allowed to edit this post.";
        }

        if (errors.hasErrors()) {
            model.addAttribute("folder", folderMapper.toResponseDto(existing.getFolder()));
            model.addAttribute("postId", postId);
            return "EditPostView";
        }

        postService.update(postId, postUpdateDto, principal.getId());

        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to delete posts!";
        }

        try {
            postService.deleteById(postId, principal.getId());
            return "redirect:/forum";
        } catch (AuthorizationException e) {
            return "ErrorView403";
        } catch (Exception e) {
            return "ErrorView404";
        }
    }

    @PostMapping("/{postId}/restore")
    public String restorePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to restore posts!";
        }

        try {
            postService.restoreById(postId, principal.getId());
            return "redirect:/forum/posts/" + postId;
        } catch (AuthorizationException e) {
            return "ErrorView403";
        } catch (Exception e) {
            return "ErrorView404";
        }
    }


    @PostMapping("/{postId}/comments")
    public String createComment(
            @PathVariable int postId,
            @Valid @ModelAttribute("commentCreationDto") CommentCreationDto commentCreationDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to comment!";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError",
                    "Comment content must be between 2 and 50 characters");
            return "redirect:/forum/posts/" + postId;
        }

        try {
            commentService.createComment(commentCreationDto, postId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Comment posted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to post comment: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/like")
    public String likeComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to like comments!";
        }

        try {
            commentService.likeComment(commentId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Comment liked!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to like comment: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/unlike")
    public String unlikeComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to unlike comments!";
        }

        try {
            commentService.unlikeComment(commentId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Comment unliked!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unlike comment: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/edit")
    public String editComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to edit comments!";
        }

        try {
            CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
            commentUpdateDto.setContent(content);
            commentService.updateComment(commentId, commentUpdateDto, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Comment updated successfully!");
        } catch (AuthorizationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to edit this comment.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update comment: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to delete comments!";
        }

        try {
            commentService.deleteById(commentId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Comment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete comment: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
    }
}