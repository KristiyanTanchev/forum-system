package com.team3.forum.repositories;

import com.team3.forum.models.Comment;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment entity);
    Comment findById(int id);
    boolean existsById(int id);
    List<Comment> findAll();
    void deleteById(int id);
    void delete(Comment entity);
    List<Comment> findByPostId(int postId);
}
