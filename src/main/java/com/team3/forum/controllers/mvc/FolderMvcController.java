package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.FolderPageHelper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.postDtos.PostPage;
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
@RequestMapping("/path")
public class FolderMvcController {
    private final FolderService folderService;
    private final FolderPageHelper folderPageHelper;
    private final PostService postService;

    @Autowired
    public FolderMvcController(FolderService folderService,
                               FolderPageHelper folderPageHelper,
                               PostService postService) {
        this.folderService = folderService;
        this.folderPageHelper = folderPageHelper;
        this.postService = postService;
    }

    @GetMapping({"/{*path}", ""})
    public String getHomeFolder(
            @PathVariable(value = "path", required = false) String path,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "date") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int tagId,
            @RequestParam(name = "siblingPage", defaultValue = "1") int siblingPage,
            @RequestParam(name = "childPage", defaultValue = "1") int childPage,
            Model model) {

        model.addAttribute("tagId", tagId);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("direction", direction);

        List<String> slugs;
        if (path == null || path.isEmpty() || path.equals("/")) {
            slugs = List.of("root");
        } else {
            slugs = List.of(path.substring(1).split("/"));
        }
        Folder folder = folderService.getFolderByPath(slugs);

        PostPage pageInfo = postService.getPostsInFolderPaginated(folder, page, search, orderBy, direction, tagId);
        model.addAttribute("pageInfo", pageInfo);

        folderPageHelper.populateSidebar(folder, siblingPage, childPage, model);

        List<PostResponseDto> mappedPosts = pageInfo.getItems();
        model.addAttribute("posts", mappedPosts);

        return "FolderView";
    }
}
