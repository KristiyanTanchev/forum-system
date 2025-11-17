package com.team3.forum.services;

import com.team3.forum.models.Comment;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    Comment createComment(CommentCreationDto dto, int postId, int userId);

    Comment updateComment(int commentId, CommentUpdateDto dto, int userId);

    Comment findById(int commentId);

    List<Comment> findAllByPostId(int postId);

    boolean existsById(int commentId);

    void deleteById(int commentId, int userId);

    void delete(Comment comment);
}
