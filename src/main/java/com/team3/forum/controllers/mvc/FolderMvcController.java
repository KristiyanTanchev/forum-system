package com.team3.forum.controllers.mvc;

import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.folderDtos.FolderResponseDto;
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
    private final static int FOLDER_PAGE_SIZE = 5;

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

        // --- path → slugs ---
        List<String> slugs;
        if (path == null || path.isEmpty() || path.equals("/")) {
            slugs = List.of("root");
        } else {
            slugs = List.of(path.substring(1).split("/"));
        }
        Folder folder = folderService.getFolderByPath(slugs);

        // posts pagination (already have PostPage)
        PostPage pageInfo = postService.getPostsInFolderPaginated(folder, page, search, orderBy, direction, tagId);
        model.addAttribute("pageInfo", pageInfo);

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

        // ---------- PARENT ----------
        if (folder.getParentFolder() != null) {
            FolderResponseDto parentFolderDto = folderMapper.toResponseDto(folder.getParentFolder());
            model.addAttribute("parent", parentFolderDto);
        } else {
            model.addAttribute("parent", null);
        }

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

        // ---------- CURRENT FOLDER / PATH ----------
        model.addAttribute("folderName", folder.getName());
        model.addAttribute("folder", folderMapper.toResponseDto(folder));


        // ---------- POSTS (already paginated by PostPage) ----------
        List<PostResponseDto> mappedPosts = pageInfo.getItems().stream()
                .map(postMapper::toResponseDto)
                .toList();
        model.addAttribute("posts", mappedPosts);

        return "FolderView";
    }
}
