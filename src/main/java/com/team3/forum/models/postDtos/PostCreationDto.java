package com.team3.forum.models.postDtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 16, max = 64, message = "Title must be between 16 and 64 characters")
    private String title;

    @NotNull
    @Size(min = 32, max = 8192, message = "Content must be between 32 and 8192 characters")
    private String content;

    @NotNull
    @Positive
    private int folderId;

    @Size(max = 50, message = "Tag must be less than 50 characters")
    @Pattern(regexp = "[A-Za-z]{0,50}", message = "Tag must be one word containing only letters")
    private String tag1;

    @Size(max = 50, message = "Tag must be less than 50 characters")
    @Pattern(regexp = "[A-Za-z]{0,50}", message = "Tag must be one word containing only letters")
    private String tag2;

    @Size(max = 50, message = "Tag must be less than 50 characters")
    @Pattern(regexp = "[A-Za-z]{0,50}", message = "Tag must be one word containing only letters")
    private String tag3;
}
