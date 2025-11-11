package com.team3.forum.models.postDtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {
    @NotNull
    @Size(min = 16, max = 64)
    private String title;

    @NotNull
    @Size(min = 32, max = 8192)
    private String content;
}
