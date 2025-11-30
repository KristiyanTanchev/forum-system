package com.team3.forum.models.postDtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreationDto {
    @NotNull
    @Size(min = 16, max = 64)
    private String title;

    @NotNull
    @Size(min = 32, max = 8192)
    private String content;

    @NotNull
    @Positive
    private int folderId;

    @Size(max = 50)
    private String tag1;

    @Size(max = 50)
    private String tag2;

    @Size(max = 50)
    private String tag3;
}
