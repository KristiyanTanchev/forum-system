package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.services.FolderService;
import com.team3.forum.services.PostService;
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

    @Autowired
    public HomeMvcController(PostService postService, PostMapper postMapper, FolderService folderService, FolderMapper folderMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.folderService = folderService;
        this.folderMapper = folderMapper;
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
        return "HomeView";
    }
}
