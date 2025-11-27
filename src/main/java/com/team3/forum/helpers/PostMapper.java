package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.services.PostService;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//TODO: Ideally mapper should not call services.
// Getting views can be done with the repository query, but time consuming.
// Consider it for future improvements.
@Component
public class PostMapper {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public PostMapper(UserService userService, PostService postService, PostService postService1) {
        this.userService = userService;
        this.postService = postService1;
    }


    public Post toEntity(PostCreationDto dto, int creatorId) {
        return Post.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .user(userService.findById(creatorId))
                .build();
    }

    public PostResponseDto toResponseDto(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .creator(post.getUser().getUsername())
                .commentsCount(post.getComments().size())
                .views(postService.getPostViews(post.getId()))
                .updatedAt(post.getUpdatedAt())
                .userId(post.getUser().getId())
                .build();
    }
}
