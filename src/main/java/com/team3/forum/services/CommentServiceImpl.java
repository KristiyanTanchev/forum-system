package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Comment;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
import com.team3.forum.repositories.CommentRepository;
import com.team3.forum.repositories.PostRepository;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    public static final String EDIT_AUTHORIZATION_ERROR = "You can only edit your own comments.";
    public static final String DELETE_AUTHORIZATION_ERROR = "You can only delete your own comments.";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Comment createComment(CommentCreationDto dto, int postId, int userId) {
        Post post = postRepository.findById(postId);
        User user = userRepository.findById(userId);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(dto.getContent());

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(int commentId, CommentUpdateDto dto, int userId) {
        Comment comment = commentRepository.findById(commentId);
        User requester = userRepository.findById(userId);

        if (!requester.isAdmin() && comment.getUser().getId() != userId) {
            throw new AuthorizationException(EDIT_AUTHORIZATION_ERROR);
        }

        comment.setContent(dto.getContent());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(int commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(int commentId) {
        return commentRepository.existsById(commentId);
    }

    @Override
    @Transactional
    public void deleteById(int commentId, int userId) {
        Comment comment = commentRepository.findById(commentId);
        User requester = userRepository.findById(userId);

        if (!requester.isAdmin() && comment.getUser().getId() != userId) {
            throw new AuthorizationException(DELETE_AUTHORIZATION_ERROR);
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}