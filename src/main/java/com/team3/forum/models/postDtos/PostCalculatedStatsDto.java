package com.team3.forum.models.postDtos;

import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.models.tagDtos.TagResponseDto;
import com.team3.forum.models.userDtos.UserResponseDto;
import lombok.*;

import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCalculatedStatsDto {
    private String creator;

    private int userId;

    private int commentsCount;

    private long views;

    private List<CommentResponseDto> comments;

    private List<UserResponseDto> likedBy;

    private String folderName;

    private List<TagResponseDto> tags;

    private String createdAtString;

    private String updatedAtString;

    private String deletedAtString;
}
