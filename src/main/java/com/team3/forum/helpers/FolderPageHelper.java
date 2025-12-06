package com.team3.forum.helpers;

import com.team3.forum.models.Folder;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.services.FolderService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
public class FolderPageHelper {

    private static final int FOLDER_PAGE_SIZE = 5;

    private final FolderService folderService;

    public FolderPageHelper(FolderService folderService) {
        this.folderService = folderService;
    }

    public void populateSidebar(Folder folder, int siblingPage, int childPage, Model model) {
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
                .map(folderService::buildFolderResponseDto)
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
                .map(folderService::buildFolderResponseDto)
                .toList();

        model.addAttribute("childFolders", childFolderResponseDtos);
        model.addAttribute("childPage", childPage);
        model.addAttribute("childTotalPages", childTotalPages);

        // ---------- PARENT ----------
        if (folder.getParentFolder() != null) {
            model.addAttribute("parent", folderService.buildFolderResponseDto(folder.getParentFolder()));
        } else {
            model.addAttribute("parent", null);
        }

        // ---------- CURRENT FOLDER ----------
        model.addAttribute("folderName", folder.getName());
        model.addAttribute("folder", folderService.buildFolderResponseDto(folder));
    }
}
