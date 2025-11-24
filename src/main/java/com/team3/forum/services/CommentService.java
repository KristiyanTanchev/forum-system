package com.team3.forum.services;

import com.team3.forum.models.Comment;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    Comment createComment(CommentCreationDto dto, int postId, User requester);

    Comment updateComment(int commentId, CommentUpdateDto dto, User requester);

    Comment findById(int commentId);

    List<Comment> findAllByPostId(int postId);

    boolean existsById(int commentId);

    void deleteById(int commentId, User requester);

    void delete(Comment comment);

    Comment restoreById(int commentId, User requester);

    int getLikes(int commentId);

    void likeComment(int commentId, User user);

    void unlikeComment(int commentId, User user);

    List<Comment> findAllByPostIdWithOrdering(int postId, String orderBy, String direction);
}