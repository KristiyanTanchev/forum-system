package com.team3.forum.models.folderDtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderCalculatedStatsDto {
    private int postCount;
    private String path;
    private int folderCount;
    private List<FolderPathDto> pathFolders;
    private int postCountWithSubfolders;
    private String lastActivity;
}
