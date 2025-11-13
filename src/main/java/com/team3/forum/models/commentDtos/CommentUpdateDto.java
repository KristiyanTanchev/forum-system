package com.team3.forum.models.commentDtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateDto {

    @NotBlank(message = "Content cannot be blank")
    private String content;
}
