package com.team3.forum.models.postDtos;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {

    private int id;

    private String title;

    private String content;

    private String creator;

    private int userId;

    private int commentsCount;

    private long views;

    private LocalDateTime updatedAt;

}
