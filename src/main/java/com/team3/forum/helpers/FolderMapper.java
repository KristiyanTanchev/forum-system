package com.team3.forum.helpers;

import com.team3.forum.models.Folder;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderPathDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.services.FolderService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
                .postCount(getFolderPostsCount(folder))
                .folderCount(folder.getChildFolders().size())
                .pathFolders(buildPathFolders(folder, new ArrayList<>()))
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

    private List<FolderPathDto> buildPathFolders(Folder folder, List<FolderPathDto> result) {
        if (folder.getParentFolder() != null) {
            buildPathFolders(folder.getParentFolder(), result);
        }
        result.add(
                FolderPathDto.builder()
                        .path(buildPath(folder, ""))
                        .slug(folder.getSlug())
                        .name(folder.getName())
                        .build()
        );
        return result;
    }

    private int getFolderPostsCount(Folder folder) {
        int sum = folder.getPosts().size();
        for (Folder child : folder.getChildFolders()) {
            sum += getFolderPostsCount(child);
        }
        return sum;
    }
}
