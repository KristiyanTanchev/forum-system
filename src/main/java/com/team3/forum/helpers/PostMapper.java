package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostCreationDto dto, User creator) {
        return Post.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .user(creator)
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
