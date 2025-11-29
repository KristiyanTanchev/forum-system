package com.team3.forum.models.userDtos;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsDto {
    private int topicCount;
    private int replyCount;
    private int likesCount;
}
