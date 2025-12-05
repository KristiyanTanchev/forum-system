package com.team3.forum.helpers;

import com.team3.forum.models.Post;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.services.FolderService;
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
    private final FolderService folderService;

    @Autowired
    public PostMapper(UserService userService, PostService postService, FolderService folderService) {
        this.userService = userService;
        this.postService = postService;
        this.folderService = folderService;
    }


    public Post toEntity(PostCreationDto dto, int creatorId) {
        return Post.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .folder(folderService.findById(dto.getFolderId()))
                .user(userService.findById(creatorId))
                .build();
    }

    public PostResponseDto toResponseDto(Post post) {
        String updatedAtString = "";
        if (post.getUpdatedAt() != null) {
            updatedAtString = TimeAgo.toTimeAgo(post.getUpdatedAt());
        }
        String deletedAtString = "";
        if (post.isDeleted()) {
            deletedAtString = TimeAgo.toTimeAgo(post.getDeletedAt());
        }
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .creator(post.getUser().getUsername())
                .commentsCount(post.getComments().size())
                .views(postService.getPostViews(post.getId()))
                .likedBy(post.getLikedBy().stream().toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .createdAtString(TimeAgo.toTimeAgo(post.getCreatedAt()))
                .updatedAtString(updatedAtString)
                .deletedAtString(deletedAtString)
                .isDeleted(post.isDeleted())
                .userId(post.getUser().getId())
                .comments(post.getComments().stream().toList())
                .folderName(post.getFolder().getName())
                .tags(post.getTags().stream().toList())
                .build();
    }
}
