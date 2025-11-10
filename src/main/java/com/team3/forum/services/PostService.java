package com.team3.forum.services;

import com.team3.forum.models.Post;

import java.util.List;

public interface PostService {
    List<Post> findAll();

    Post findById(int id);

    void deleteById(int id);

    Post create(Post post);

    Post update(Post post);
}
