package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Post;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/forum/posts")
public class PostMvcController {
    private final PostService postService;
    private final PostMapper postMapper;

    @Autowired
    public PostMvcController(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping("/{postId}")
    public String getPostPage(
            Model model,
            @PathVariable int postId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Post post = postService.findById(postId);
        if (principal != null) {
            postService.registerView(postId, principal.getId());
        }
        model.addAttribute("post", postMapper.toResponseDto(post));
        return "PostView";
    }
}
