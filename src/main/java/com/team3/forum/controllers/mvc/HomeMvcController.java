package com.team3.forum.controllers.mvc;

import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.services.FolderService;
import com.team3.forum.services.PostService;
import com.team3.forum.services.TagService;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/forum")
public class HomeMvcController {

    private final PostService postService;
    private final FolderService folderService;
    private final TagService tagService;
    private final UserService userService;

    @Autowired
    public HomeMvcController(PostService postService, FolderService folderService, TagService tagService, UserService userService) {
        this.postService = postService;
        this.folderService = folderService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public String getHomePage(Model model) {
        List<Folder> folders = folderService.findHomeFolders();
        List<FolderResponseDto> folderResponseDtos = folders
                .stream().map(folderService::buildFolderResponseDto).toList();
        model.addAttribute("folders", folderResponseDtos);

        List<Post> posts = postService.getTrendingPosts();
        List<PostResponseDto> mappedPosts = posts.stream()
                .map(postService::buildPostResponseDto)
                .toList();
        model.addAttribute("posts", mappedPosts);

        List<TagResponseDto> tags = tagService.findTopByOrderByPostsCountDesc(5).stream()
                .map(tag -> TagResponseDto.builder().name(tag.getName()).id(tag.getId()).build())
                .toList();

        model.addAttribute("tags", tags);

        model.addAttribute("usersCount", userService.getUsersCount());

        return "HomeView";
    }

    @GetMapping("/about")
    public String getAboutPage() {
        return "AboutView";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "ContactView";
    }

    @GetMapping("/api-info")
    public String apiInfoPage() {
        return "ApiInfoView";
    }
}
