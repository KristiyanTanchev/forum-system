package com.team3.forum.models.userDtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDto {
    private int userId;
    private String username;

}
