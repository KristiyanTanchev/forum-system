package com.team3.forum.helpers;

import com.team3.forum.models.Folder;
import com.team3.forum.models.MediaMetaData;
import com.team3.forum.models.folderDtos.*;
import org.springframework.stereotype.Component;


@Component
public class FolderMapper {

    public Folder toEntity(FolderCreateDto dto) {
        return Folder.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .build();
    }

    public FolderPathDto toPathDto(Folder folder) {
        return FolderPathDto.builder()
                .name(folder.getName())
                .slug(folder.getSlug())
                .description(folder.getDescription())
                .path(buildPath(folder, ""))
                .build();
    }

    public FolderUpdateDto toUpdateDto(Folder folder) {
        return FolderUpdateDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .slug(folder.getSlug())
                .description(folder.getDescription())
                .build();
    }


    public FolderResponseDto toResponseDto(Folder folder,
                                           FolderCalculatedStatsDto folderCalculatedStatsDto,
                                           MediaMetaData metaData
    ) {
        return FolderResponseDto.builder()
                .name(folder.getName())
                .slug(folder.getSlug())
                .description(folder.getDescription())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .id(folder.getId())
                .postCount(folderCalculatedStatsDto.getPostCount())
                .postCountWithSubfolders(folderCalculatedStatsDto.getPostCountWithSubfolders())
                .folderCount(folderCalculatedStatsDto.getFolderCount())
                .pathFolders(folderCalculatedStatsDto.getPathFolders())
                .lastActivity(folderCalculatedStatsDto.getLastActivity())
                .path(folderCalculatedStatsDto.getPath())
                .metaData(metaData)
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
