package com.team3.forum.models.folderDtos;

import com.team3.forum.models.MediaMetaData;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponseDto {
    private int id;
    private String name;
    private String slug;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int postCount;
    private String path;
    private int folderCount;
    private List<FolderPathDto> pathFolders;
    private int postCountWithSubfolders;
    private String lastActivity;
    private MediaMetaData metaData;
}
