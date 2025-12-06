package com.team3.forum.controllers.mvc;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.exceptions.FolderNotEmptyException;
import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.FolderPageHelper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.folderDtos.FolderUpdateDto;
import com.team3.forum.security.CustomUserDetails;
import com.team3.forum.services.FolderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/folders")
public class FoldersMvcController {
    private final static int FOLDER_PAGE_SIZE = 5;

    private final FolderService folderService;
    private final FolderPageHelper folderPageHelper;
    private final FolderMapper folderMapper;

    @Autowired
    public FoldersMvcController(FolderService folderService,
                                FolderPageHelper folderPageHelper, FolderMapper folderMapper) {
        this.folderService = folderService;
        this.folderPageHelper = folderPageHelper;
        this.folderMapper = folderMapper;
    }

    @GetMapping({"", "/"})
    public String getFolderPage(
            @RequestParam(defaultValue = "0") int folderId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "date") String orderBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int tagId,
            @RequestParam(name = "siblingPage", defaultValue = "1") int siblingPage,
            @RequestParam(name = "childPage", defaultValue = "1") int childPage) {

        Folder folder;
        if (folderId == 0) {
            folder = folderService.findHomeFolders().get(0);
        } else {
            folder = folderService.findById(folderId);
        }

        List<String> slugs = folderService.buildSlugPath(folder);
        String path = String.join("/", slugs);

        StringBuilder qs = new StringBuilder();
        qs.append("?page=").append(page);
        if (!search.isEmpty()) {
            qs.append("&search=").append(search);
        }
        qs.append("&orderBy=").append(orderBy);
        qs.append("&direction=").append(direction);
        qs.append("&tagId=").append(tagId);
        qs.append("&siblingPage=").append(siblingPage);
        qs.append("&childPage=").append(childPage);

        return "redirect:/path/" + path + qs;
    }

    @GetMapping("/new")
    public String createFolderPage(
            Model model,
            @RequestParam(defaultValue = "0") int folderId,
            @RequestParam(defaultValue = "1") int siblingPage,
            @RequestParam(defaultValue = "1") int childPage,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to create a folder!";
        }

        Folder parentFolder = (folderId == 0)
                ? folderService.findHomeFolders().get(0)
                : folderService.findById(folderId);

        folderPageHelper.populateSidebar(parentFolder, siblingPage, childPage, model);

        FolderCreateDto form = FolderCreateDto.builder()
                .parentFolderId(parentFolder.getId())
                .build();
        model.addAttribute("folderForm", form);

        return "CreateFolderView";
    }

    @PostMapping("/new")
    public String createFolderForm(
            @Valid @ModelAttribute("folderForm") FolderCreateDto folderCreateDto,
            BindingResult bindingResult,
            @RequestParam(defaultValue = "1") int siblingPage,
            @RequestParam(defaultValue = "1") int childPage,
            @AuthenticationPrincipal CustomUserDetails principal,
            Model model) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to create a folder!";
        }

        Folder parentFolder;
        if (folderCreateDto.getParentFolderId() == 0) {
            parentFolder = folderService.findHomeFolders().get(0);
        } else {
            parentFolder = folderService.findById(folderCreateDto.getParentFolderId());
        }

        if (bindingResult.hasErrors()) {
            folderPageHelper.populateSidebar(parentFolder, siblingPage, childPage, model);
            model.addAttribute("folderName", parentFolder.getName());
            model.addAttribute("folder", folderService.buildFolderResponseDto(parentFolder));

            return "CreateFolderView";
        }
        try {
            Folder created = folderService.create(folderCreateDto, principal.getId());
            return "redirect:/folders?folderId=" + created.getId();
        } catch (EntityUpdateConflictException e) {
            bindingResult.rejectValue("slug", "folder.slug.conflict", e.getMessage());
            folderPageHelper.populateSidebar(parentFolder, siblingPage, childPage, model);
            model.addAttribute("folderName", parentFolder.getName());
            model.addAttribute("folder", folderService.buildFolderResponseDto(parentFolder));
            model.addAttribute("creating", true);
            return "CreateFolderView";
        }
    }

    @GetMapping("/{folderId}/edit")
    public String editFolderPage(
            @PathVariable int folderId,
            Model model) {
        Folder folder = folderService.findById(folderId);
        FolderResponseDto folderDto = folderService.buildFolderResponseDto(folder);

        FolderResponseDto parentDto = null;
        if (folder.getParentFolder() != null) {
            parentDto = folderService.buildFolderResponseDto(folder.getParentFolder());
        }

        FolderUpdateDto form = folderMapper.toUpdateDto(folder);

        model.addAttribute("folder", folderDto);
        model.addAttribute("parent", parentDto);
        model.addAttribute("folderForm", form);

        return "EditFolderView";
    }

    @PostMapping("/edit")
    public String editFolderForm(
            @Valid @ModelAttribute("folderForm") FolderUpdateDto folderForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails principal,
            Model model) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to edit a folder!";
        }

        Folder folder = folderService.findById(folderForm.getId());
        Folder parent = folder.getParentFolder();

        var folderDto = folderService.buildFolderResponseDto(folder);
        var parentDto = (parent != null) ? folderService.buildFolderResponseDto(parent) : null;

        if (bindingResult.hasErrors()) {
            model.addAttribute("folder", folderDto);
            model.addAttribute("parent", parentDto);
            return "EditFolderView";
        }

        try {
            folderService.update(folderForm, principal.getId());

            return "redirect:/folders?folderId=" + folder.getId();

        } catch (AuthorizationException e) {
            bindingResult.reject("folder.auth", e.getMessage());
            model.addAttribute("folder", folderDto);
            model.addAttribute("parent", parentDto);
            return "EditFolderView";
        } catch (EntityUpdateConflictException e) {
            bindingResult.rejectValue("slug", "folder.slug.conflict", e.getMessage());
            model.addAttribute("folder", folderDto);
            model.addAttribute("parent", parentDto);
            return "EditFolderView";
        }
    }

    @PostMapping("/{folderId}/delete")
    public String deleteFolder(
            @PathVariable int folderId,
            @AuthenticationPrincipal CustomUserDetails principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/auth/login?error=You must be logged in to delete a folder!";
        }

        Folder folder = folderService.findById(folderId);
        Folder parent = folder.getParentFolder();

        try {
            folderService.deleteById(folderId, principal.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Folder deleted successfully.");
            Folder target = (parent != null)
                    ? parent
                    : folderService.findHomeFolders().get(0);
            String path = String.join("/", folderService.buildSlugPath(target));
            return "redirect:/path/" + path;

        } catch (AuthorizationException | EntityUpdateConflictException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            String path = String.join("/", folderService.buildSlugPath(folder));
            return "redirect:/path/" + path;
        } catch (FolderNotEmptyException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Folder is not empty.");

            String path = String.join("/", folderService.buildSlugPath(folder));
            return "redirect:/path/" + path;
        }
    }
}
