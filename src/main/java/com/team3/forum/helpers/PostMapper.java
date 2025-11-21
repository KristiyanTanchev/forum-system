package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    private final UserService userService;

    @Autowired
    public PostMapper(UserService userService) {
        this.userService = userService;
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
                .userId(post.getUser().getId())
                .build();
    }
}
