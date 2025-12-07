package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.postDtos.PostCalculatedStatsDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostCreationDto dto) {
        return Post.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .build();
    }

    public PostResponseDto toResponseDto(Post post, PostCalculatedStatsDto postCalculatedStatsDto) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isDeleted(post.isDeleted())
                .updatedAt(post.getUpdatedAt())
                .createdAt(post.getCreatedAt())
                .creator(postCalculatedStatsDto.getCreator())
                .commentsCount(postCalculatedStatsDto.getCommentsCount())
                .views(postCalculatedStatsDto.getViews())
                .likedBy(postCalculatedStatsDto.getLikedBy())
                .createdAtString(postCalculatedStatsDto.getCreatedAtString())
                .updatedAtString(postCalculatedStatsDto.getUpdatedAtString())
                .deletedAtString(postCalculatedStatsDto.getDeletedAtString())
                .userId(postCalculatedStatsDto.getUserId())
                .comments(postCalculatedStatsDto.getComments())
                .folderName(post.getFolder().getName())
                .tags(postCalculatedStatsDto.getTags())
                .build();
    }
}
