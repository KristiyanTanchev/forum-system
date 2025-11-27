package com.team3.forum.models.postDtos;

import com.team3.forum.models.Post;

public record PostWithViewsDto(Post post, long viewCount) {
}