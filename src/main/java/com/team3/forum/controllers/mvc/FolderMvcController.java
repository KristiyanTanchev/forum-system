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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/forum/root")
public class FolderMvcController {
    private final FolderService folderService;
    private final FolderMapper folderMapper;
    private final PostMapper postMapper;
    private final PostService postService;

    @Autowired
    public FolderMvcController(FolderService folderService, FolderMapper folderMapper, PostMapper postMapper, PostService postService) {
        this.folderService = folderService;
        this.folderMapper = folderMapper;
        this.postMapper = postMapper;
        this.postService = postService;
    }

    @GetMapping
    public String getHomeFolder(Model model) {
        List<FolderResponseDto> childFolderResponseDtos = folderService.findHomeFolders().stream()
                .map(folderMapper::toResponseDto).toList();
        model.addAttribute("folderName", "Root folder");
        model.addAttribute("childFolders", childFolderResponseDtos);

        FolderResponseDto folderResponseDto = FolderResponseDto.builder()
                .name("Root folder")
                .slug("")
                .build();
        model.addAttribute("folder", folderResponseDto);


        List<Post> posts = postService
                .getPostsInFolderPaginated(null, 1, "date", "desc");

        List<PostResponseDto> mappedPosts = posts.stream()
                .map(postMapper::toResponseDto)
                .toList();
        model.addAttribute("posts", mappedPosts);
        return "FolderView";
    }

    @GetMapping("/{*path}")
    public String getHomeFolder(
            @PathVariable("path") String path,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "date") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {
        if (path.isEmpty() || path.equals("/")) {
            return "redirect:/forum/root";
        }
        List<String> slugs = List.of(path.substring(1).split("/"));
        if (slugs.isEmpty()) {
            return "redirect:/forum/root";
        }
        Folder folder = folderService.getFolderByPath(slugs);

        List<Folder> siblingFolders = folderService.getSiblingFolders(folder);
        List<FolderResponseDto> siblingFolderResponseDtos = siblingFolders.stream()
                .map(folderMapper::toResponseDto).toList();

        if (folder.getParentFolder() != null) {
            FolderResponseDto parentFolderDto = folderMapper.toResponseDto(folder.getParentFolder());
            model.addAttribute("parent", parentFolderDto);
        }
        if (folder.getParentFolder() == null) {
            FolderResponseDto parentFolderDto = FolderResponseDto.builder()
                    .name("Root folder")
                    .slug("")
                    .build();
            model.addAttribute("parent", parentFolderDto);
        }

        model.addAttribute("siblingFolders", siblingFolderResponseDtos);

        model.addAttribute("folderName", folder.getName());

        model.addAttribute("folder", folderMapper.toResponseDto(folder));

        List<FolderResponseDto> childFolderResponseDtos = folder.getChildFolders().stream()
                .sorted((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()))
                .map(folderMapper::toResponseDto).toList();

        model.addAttribute("childFolders", childFolderResponseDtos);

        List<Post> posts = postService
                .getPostsInFolderPaginated(folder, 1, "date", "desc");


        List<PostResponseDto> mappedPosts = posts.stream()
                .map(postMapper::toResponseDto)
                .toList();
        model.addAttribute("posts", mappedPosts);
        return "FolderView";
    }
}
