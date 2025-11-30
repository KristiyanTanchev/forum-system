package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.services.FolderService;
import com.team3.forum.services.PostService;
import com.team3.forum.services.TagService;
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
    private final PostMapper postMapper;
    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final TagService tagService;

    @Autowired
    public HomeMvcController(PostService postService, PostMapper postMapper, FolderService folderService, FolderMapper folderMapper, TagService tagService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.folderService = folderService;
        this.folderMapper = folderMapper;
        this.tagService = tagService;
    }

    @GetMapping
    public String getHomePage(Model model) {
        List<Folder> folders = folderService.findHomeFolders();
        List<FolderResponseDto> folderResponseDtos = folders
                .stream().map(folderMapper::toResponseDto).toList();
        model.addAttribute("folders", folderResponseDtos);

        List<Post> posts = postService.getTrendingPosts();
        List<PostResponseDto> mappedPosts = posts.stream()
                .map(postMapper::toResponseDto)
                .toList();
        model.addAttribute("posts", mappedPosts);

        List<TagResponseDto> tags = tagService.findTopByOrderByPostsCountDesc(5).stream()
                .map(tag -> TagResponseDto.builder().name(tag.getName()).id(tag.getId()).build())
                .toList();

        model.addAttribute("tags", tags);

        return "HomeView";
    }
}
