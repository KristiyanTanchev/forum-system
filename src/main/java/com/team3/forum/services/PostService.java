package com.team3.forum.services;

import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.postDtos.PostUpdateDto;

import java.util.List;

public interface PostService {
    List<Post> findAll();

    Post findById(int id);

    void deleteById(int id, User requester);

    Post create(Post post);

    Post update(int postId, PostUpdateDto postUpdateDto, User requester);

    int getLikes(int postId);

    void likePost(int postId, int userId);

    void unlikePost(int postId, int userId);
}
