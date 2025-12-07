package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.Tag;
import com.team3.forum.models.postDtos.PostCalculatedStatsDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.postDtos.PostUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    private final MarkdownService markdownService;

    @Autowired
    public PostMapper(MarkdownService markdownService) {
        this.markdownService = markdownService;
    }

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
                .content(markdownService.toHtml(post.getContent()))
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

    public PostUpdateDto toUpdateDto(Post post) {
        List<Tag> tags = post.getTags().stream().toList();
        return PostUpdateDto.builder()
                .content(post.getContent())
                .title(post.getTitle())
                .tag1(tags.size() > 0 ? tags.get(0).getName() : null)
                .tag2(tags.size() > 1 ? tags.get(1).getName() : null)
                .tag3(tags.size() > 2 ? tags.get(2).getName() : null)
                .build();
    }
}
