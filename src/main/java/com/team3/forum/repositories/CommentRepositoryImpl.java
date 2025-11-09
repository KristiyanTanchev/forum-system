package com.team3.forum.repositories;

import com.team3.forum.models.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    @Override
    public Comment save(Comment entity) {
        return null;
    }

    @Override
    public Comment findById(int id) {
        return null;
    }

    @Override
    public boolean existsById(int id) {
        return false;
    }

    @Override
    public List<Comment> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public void delete(Comment entity) {

    }
}
