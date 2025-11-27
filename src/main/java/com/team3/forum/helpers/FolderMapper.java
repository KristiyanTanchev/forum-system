package com.team3.forum.helpers;

import com.team3.forum.models.Folder;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.services.FolderService;
import org.springframework.stereotype.Component;

//TODO: Ideally mapper should not call services.
// Getting views can be done with the repository query, but time consuming.
// Consider it for future improvements.
@Component
public class FolderMapper {
    private final FolderService folderService;

    public FolderMapper(FolderService folderService) {
        this.folderService = folderService;
    }

    public Folder toEntity(FolderCreateDto dto) {
        Folder parent = null;
        if (dto.getParentFolderId() != 0){
            parent = folderService.findById(dto.getParentFolderId());
        }
        return Folder.builder()
                .name(dto.getName())
                .parentFolder(parent)
                .slug(dto.getSlug())
                .build();
    }

    public FolderResponseDto toResponseDto(Folder folder) {
        return FolderResponseDto.builder()
                .name(folder.getName())
                .slug(folder.getSlug())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .id(folder.getId())
                .postCount(folder.getPosts().size())
                .folderCount(folder.getChildFolders().size())
                .path(buildPath(folder, ""))
                .build();
    }

    private String buildPath(Folder folder, String path) {
        StringBuilder sb = new StringBuilder();
        if (folder.getParentFolder() != null) {
            sb.append(buildPath(folder.getParentFolder(), path)).append("/");
        }
        return sb.append(path).append(folder.getSlug()).toString();
    }
}
