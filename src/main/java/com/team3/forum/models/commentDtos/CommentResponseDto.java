package com.team3.forum.models.commentDtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private int id;
    private int postId;
    private int userId;
    private String content;
}
