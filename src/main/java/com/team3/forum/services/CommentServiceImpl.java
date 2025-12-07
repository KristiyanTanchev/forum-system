package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.helpers.CommentMapper;
import com.team3.forum.models.Comment;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentResponseDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
import com.team3.forum.models.enums.Role;
import com.team3.forum.repositories.CommentRepository;
import com.team3.forum.repositories.PostRepository;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {
    public static final String ALREADY_LIKED_ERROR = "This comment is already liked by this user.";
    public static final String NOT_LIKED_ERROR = "This comment is not liked by this user.";
    public static final String EDIT_AUTHORIZATION_ERROR = "You cannot edit this comment.";
    public static final String DELETE_AUTHORIZATION_ERROR = "You cannot delete this comment.";

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository,
                              UserRepository userRepository,
                              CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public Comment createComment(CommentCreationDto dto, int postId, int userId) {
        User user = userRepository.findById(userId);
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Post", postId);
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setDeleted(false);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(int commentId, CommentUpdateDto dto, int userId) {
        User user = userRepository.findById(userId);
        Comment comment = commentRepository.findById(commentId);
        if (comment == null || comment.isDeleted()) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        verifyModeratorOrOwner(comment, user, new AuthorizationException(EDIT_AUTHORIZATION_ERROR));
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByPostId(int postId) {
        return commentRepository.findByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null || comment.isDeleted()) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        return comment != null && !comment.isDeleted();
    }

    @Override
    @Transactional
    public void deleteById(int commentId, int userId) {
        User user = userRepository.findById(userId);
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        if (comment.isDeleted()) {
            throw new EntityUpdateConflictException(String.format("Comment with id %d is already deleted.", commentId));
        }
        verifyModeratorOrOwner(comment, user, new AuthorizationException(DELETE_AUTHORIZATION_ERROR));
        comment.setDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public Comment restoreById(int commentId, int userId) {
        User user = userRepository.findById(userId);
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        verifyModeratorOrOwner(comment, user, new AuthorizationException(DELETE_AUTHORIZATION_ERROR));
        if (!comment.isDeleted()) {
            throw new EntityUpdateConflictException(String.format("Comment with id %d is not deleted.", commentId));
        }
        comment.setDeleted(false);
        comment.setDeletedAt(null);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public int getLikes(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null || comment.isDeleted()) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        return comment.getLikedBy().size();
    }

    @Override
    @Transactional
    public void likeComment(int commentId, int userId) {
        User user = userRepository.findById(userId);
        Comment comment = commentRepository.findById(commentId);
        if (comment == null || comment.isDeleted()) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        if (comment.getLikedBy().contains(user)) {
            throw new DuplicateEntityException(ALREADY_LIKED_ERROR);
        }
        comment.getLikedBy().add(user);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void unlikeComment(int commentId, int userId) {
        User user = userRepository.findById(userId);
        Comment comment = commentRepository.findById(commentId);
        if (comment == null || comment.isDeleted()) {
            throw new EntityNotFoundException("Comment", commentId);
        }
        if (!comment.getLikedBy().contains(user)) {
            throw new EntityNotFoundException(NOT_LIKED_ERROR);
        }
        comment.getLikedBy().remove(user);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAllByPostIdWithOrdering(int postId, String orderBy, String direction) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        Comparator<Comment> comparator;
        switch (orderBy.toLowerCase()) {
            case "likes":
                comparator = Comparator.comparingInt(comment -> comment.getLikedBy().size());
                break;
            case "created_at":
            default:
                comparator = Comparator.comparing(Comment::getCreatedAt);
                break;
        }
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        return comments.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public int getCommentCount() {
        return commentRepository.getCommentCount();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDto buildCommentResponseDto(Comment comment) {
        return commentMapper.toResponseDto(comment, null);
    }

    private void verifyModeratorOrOwner(Comment comment, User requester, RuntimeException error) {

        if (comment.getUser().getId() == requester.getId()) {
            return;
        }


        if (requester.isModerator()) {
            Role targetRole = comment.getUser().getRole();
            if (targetRole == Role.USER) {
                return;
            }
            if (requester.isAdmin()) {
                return;
            }
        }

        throw error;
    }
}