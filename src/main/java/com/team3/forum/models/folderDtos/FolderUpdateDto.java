package com.team3.forum.models.folderDtos;

import com.team3.forum.models.Folder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderUpdateDto {
    @NotNull
    @Size(min = 1, max = 32)
    private String name;

    @NotNull
    @Size(min = 1, max = 32)
    private String slug;
}
