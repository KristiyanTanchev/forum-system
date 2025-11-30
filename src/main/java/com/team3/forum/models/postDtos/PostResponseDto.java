package com.team3.forum.models.postDtos;

import com.team3.forum.models.Comment;
import com.team3.forum.models.Tag;
import com.team3.forum.models.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {

    private int id;

    private String title;

    private String content;

    private String creator;

    private int userId;

    private int commentsCount;

    private long views;

    private LocalDateTime updatedAt;

    private List<Comment> comments;

    private List<User> likedBy;

    private String folderName;

    private List<Tag> tags;

}
