package com.team3.forum.models.userDtos;

import com.team3.forum.models.User;
import lombok.*;

import java.util.List;
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPage {
    private List<User> items;
    private int page;
    private int size;
    private int totalItems;
    private int totalPages;
    private int fromItem;
    private int toItem;
}
