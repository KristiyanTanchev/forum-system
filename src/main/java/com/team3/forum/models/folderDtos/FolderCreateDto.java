package com.team3.forum.models.folderDtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderCreateDto {
    @NotNull
    private int parentFolderId;

    @NotNull
    @Size(min = 1, max = 32)
    private String name;

    @NotNull
    @Size(min = 1, max = 32)
    private String slug;

    @Size(min = 0, max = 255)
    private String description;
}
