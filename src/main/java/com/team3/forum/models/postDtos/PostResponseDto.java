package com.team3.forum.models.postDtos;

import lombok.*;


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

    private int userId;

    private long views;

}
