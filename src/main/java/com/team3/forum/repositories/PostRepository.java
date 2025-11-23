package com.team3.forum.repositories;

import com.team3.forum.models.Post;

import java.util.List;

public interface PostRepository {
    Post save(Post entity);

    Post findById(int id);

    boolean existsById(int id);

    List<Post> findAll();

    void deleteById(int id);

    void delete(Post entity);

    Post findByAndIsDeleted(int id);
}
