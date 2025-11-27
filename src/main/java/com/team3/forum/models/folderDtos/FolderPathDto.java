package com.team3.forum.models.folderDtos;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderPathDto {
    private String path;
    private String slug;
    private String name;
}
