package com.team3.forum.controllers.mvc;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.helpers.CommentMapper;
import com.team3.forum.helpers.FolderPageHelper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.*;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
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

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/forum/posts")
public class PostMvcController {

    private final PostService postService;
    private final FolderService folderService;
    private final TagService tagService;
    private final CommentService commentService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final FolderPageHelper folderPageHelper;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @Autowired
    public PostMvcController(PostService postService,
                             FolderService folderService,
                             TagService tagService,
                             CommentService commentService,
                             UserService userService,
                             FolderPageHelper folderPageHelper,
                             CommentMapper commentMapper,
                             UserMapper userMapper,
                             PostMapper postMapper) {
        this.postService = postService;
        this.folderService = folderService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
        this.folderPageHelper = folderPageHelper;
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
        this.postMapper = postMapper;
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
        List<PostResponseDto> posts = pageInfo.getItems();

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

        model.addAttribute("tags", tagService.findAllWithPostsCount());
        return "AllPostsView";
    }

    @GetMapping("/{postId}")
    public String getPostPage(
            Model model,
            @PathVariable int postId,
            @RequestParam(defaultValue = "date") String sortCommentsBy,
            @RequestParam(defaultValue = "0") int commentPage,
            @RequestParam(defaultValue = "10") int commentSize,
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

        if (principal != null && (principal.isModerator() || post.getUser().getId() == principal.getId())) {
            model.addAttribute("canEdit", true);
        }

        model.addAttribute("tags",
                post.getTags().stream()
                        .map(tag -> TagResponseDto.builder().id(tag.getId()).name(tag.getName()).build())
                        .toList()
        );

        model.addAttribute("post", postService.buildPostResponseDto(post));
        User currentUser = principal != null ? userService.findById(principal.getId()) : null;

        List<Comment> comments;
        if ("likes".equals(sortCommentsBy)) {
            comments = commentService.findAllByPostIdWithOrdering(postId, "likes", "desc");
        } else {
            comments = commentService.findAllByPostId(postId);
        }

        int totalComments = comments.size();
        int totalPages = (int) Math.ceil((double) totalComments / commentSize);
        int start = commentPage * commentSize;
        int end = Math.min(start + commentSize, totalComments);

        List<Comment> paginatedComments;
        if (start >= totalComments) {
            paginatedComments = List.of();
        } else {
            paginatedComments = comments.subList(start, end);
        }

        paginatedComments.forEach(comment -> {
            comment.getLikedBy().size();
        });


        List<CommentResponseDto> commentDtos = paginatedComments.stream()
                .map(comment -> commentMapper.toResponseDto(comment, currentUser))
                .toList();

        model.addAttribute("comments", commentDtos);
        model.addAttribute("commentPage", commentPage);
        model.addAttribute("commentTotalPages", totalPages);
        if (totalComments == 0) {
            model.addAttribute("commentFromItem", 0);
            model.addAttribute("commentToItem", 0);
        } else {
            model.addAttribute("commentFromItem", start + 1);
            model.addAttribute("commentToItem", end);
        }
        model.addAttribute("commentTotalItems", totalComments);

        model.addAttribute("currentUser", currentUser);
        if (currentUser != null) {
            model.addAttribute("currentUserDto", userMapper.toResponseDto(currentUser));
        }
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
        Folder folder;
        if (folderId == 0) {
            folder = folderService.findHomeFolders().get(0);
        } else {
            folder = folderService.findById(folderId);
        }

        folderPageHelper.populateSidebar(folder, siblingPage, childPage, model);

        PostCreationDto postCreationDto = new PostCreationDto();
        postCreationDto.setFolderId(folder.getId());
        model.addAttribute("post", postCreationDto);
        model.addAttribute("tags", tagService.findAll());
        return "CreatePostView";
    }

    @PostMapping("/new")
    public String createPost(
            Model model,
            @Valid @ModelAttribute("post") PostCreationDto postCreationDto,
            BindingResult errors,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (errors.hasErrors()) {
            Folder folder = folderService.findById(postCreationDto.getFolderId());
            folderPageHelper.populateSidebar(folder, 1, 1, model);
            postCreationDto.setFolderId(folder.getId());
            model.addAttribute("post", postCreationDto);
            model.addAttribute("tags", tagService.findAll());
            return "CreatePostView";
        }
        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to create a post!";
        }

        Post post = postService.create(postCreationDto, principal.getId());
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

        if (!principal.isModerator() && post.getUser().getId() != principal.getId()) {
            return "redirect:/forum/posts/" + postId + "?error=You are not allowed to edit this post.";
        }

        PostUpdateDto dto = postMapper.toUpdateDto(post);

        Folder folder = post.getFolder();
        List<Tag> allTags = tagService.findAll().stream().sorted(Comparator.comparing(Tag::getName)).toList();
        model.addAttribute("folder", folderService.buildFolderResponseDto(folder));
        model.addAttribute("post", dto);
        model.addAttribute("postId", postId);
        model.addAttribute("tags", allTags);

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

        if (!principal.isModerator() && existing.getUser().getId() != principal.getId()) {
            return "redirect:/forum/posts/" + postId + "?error=You are not allowed to edit this post.";
        }

        if (errors.hasErrors()) {
            model.addAttribute("folder", folderService.buildFolderResponseDto(existing.getFolder()));
            model.addAttribute("postId", postId);
            model.addAttribute("tags", tagService.findAll());
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


    @PostMapping("/{postId}/like")
    public String likePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to like comments!";
        }

        try {
            postService.likePost(postId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Post liked!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to like post: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
    }

    @PostMapping("/{postId}/unlike")
    public String unlikePost(
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to unlike comments!";
        }

        try {
            postService.unlikePost(postId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Post unliked!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unlike post: " + e.getMessage());
        }

        return "redirect:/forum/posts/" + postId;
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