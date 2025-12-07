package com.team3.forum.models.postDtos;

import lombok.*;

import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPage {

    List<PostResponseDto> items;

    int page;

    int size;

    int totalItems;

    int totalPages;

    int fromItem;

    int toItem;

    int tagId;

    String searchQuery;
}
