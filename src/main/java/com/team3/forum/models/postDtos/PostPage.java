package com.team3.forum.models.postDtos;

import com.team3.forum.models.Post;
import lombok.*;

import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPage {

    List<Post> items;

    int page;

    int size;

    int totalItems;

    int totalPages;

    int fromItem;

    int toItem;

    int tagId;
}
