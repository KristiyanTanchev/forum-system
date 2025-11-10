package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    private final UserRepository userRepository;

    @Autowired
    public PostMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Post toEntity(PostCreationDto dto) {
        return Post.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .user(
                        userRepository.findById(dto.getUserId())
                )
                .build();
    }

    public Post toEntity(PostCreationDto dto, int id) {
        Post post = this.toEntity(dto);
        post.setId(id);
        return post;
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
