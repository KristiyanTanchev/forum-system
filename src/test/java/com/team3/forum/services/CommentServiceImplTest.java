package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Comment;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
import com.team3.forum.models.enums.Role;
import com.team3.forum.repositories.CommentRepository;
import com.team3.forum.repositories.PostRepository;
import com.team3.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    CommentRepository mockCommentRepository;

    @Mock
    PostRepository mockPostRepository;

    @Mock
    UserRepository mockUserRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    public void findAllByPostId_Should_Call_Repository() {
        // Arrange
        User user = createMockUser();
        Post post = new Post();
        post.setId(1);
        Comment comment = createComment(1, user, post);

        Mockito.when(mockCommentRepository.findByPostId(1)).thenReturn(List.of(comment));

        // Act
        List<Comment> result = commentService.findAllByPostId(1);

        // Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).findByPostId(1);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(comment, result.get(0));
    }

    @Test
    public void findById_Should_Call_Repository() {
        // Arrange
        User user = createMockUser();
        Comment mockComment = createComment(1, user, new Post());

        Mockito.when(mockCommentRepository.findById(anyInt())).thenReturn(mockComment);

        // Act
        Comment result = commentService.findById(mockComment.getId());

        // Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).findById(1);
        Assertions.assertEquals(result, mockComment);
    }

    @Test
    public void findById_WithDeletedComment_Should_Throw_EntityNotFoundException() {
        // Arrange
        User user = createMockUser();
        Comment deletedComment = createComment(1, user, new Post());
        deletedComment.setDeleted(true);

        Mockito.when(mockCommentRepository.findById(1)).thenReturn(deletedComment);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                commentService.findById(1));
    }

    @Test
    public void create_WithValidData_Should_Call_Repository() {
        // Arrange
        User user = createMockUser();
        Post post = new Post();
        post.setId(1);
        CommentCreationDto dto = new CommentCreationDto();
        dto.setContent("Test comment");

        Mockito.when(mockUserRepository.findById(user.getId())).thenReturn(user);
        Mockito.when(mockPostRepository.findById(1)).thenReturn(post);
        Mockito.when(mockCommentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Comment result = commentService.createComment(dto, 1, user.getId());

        // Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
        Assertions.assertEquals("Test comment", result.getContent());
        Assertions.assertEquals(user, result.getUser());
        Assertions.assertEquals(post, result.getPost());
        Assertions.assertFalse(result.isDeleted());
    }

    @Test
    public void create_WithNonExistingPost_Should_Throw_EntityNotFoundException() {
        // Arrange
        User user = createMockUser();
        CommentCreationDto dto = new CommentCreationDto();
        dto.setContent("Test comment");

        Mockito.when(mockUserRepository.findById(user.getId())).thenReturn(user);
        Mockito.when(mockPostRepository.findById(1)).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                commentService.createComment(dto, 1, user.getId()));
    }

    @Test
    public void update_WithCommentOwner_Should_Call_Repository() {
        // Arrange
        User commentOwner = createMockUser();
        Post post = new Post();
        Comment existingComment = createComment(1, commentOwner, post);
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setContent("Updated content");

        Mockito.when(mockUserRepository.findById(commentOwner.getId())).thenReturn(commentOwner);
        Mockito.when(mockCommentRepository.findById(1)).thenReturn(existingComment);
        Mockito.when(mockCommentRepository.save(existingComment)).thenReturn(existingComment);

        // Act
        Comment result = commentService.updateComment(1, dto, commentOwner.getId());

        // Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(existingComment);
        Assertions.assertEquals("Updated content", result.getContent());
        Assertions.assertTrue(result.getUpdatedAt().isAfter(result.getCreatedAt()));
    }

    @Test
    public void update_WithNonOwnerUser_Should_Throw_AuthorizationException() {
        // Arrange
        User commentOwner = createMockUser();
        commentOwner.setId(1);
        User otherUser = createMockUser();
        otherUser.setId(2);

        Post post = new Post();
        Comment existingComment = createComment(1, commentOwner, post);
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setContent("Updated content");

        Mockito.when(mockUserRepository.findById(otherUser.getId())).thenReturn(otherUser);
        Mockito.when(mockCommentRepository.findById(1)).thenReturn(existingComment);

        // Act & Assert
        Assertions.assertThrows(AuthorizationException.class, () ->
                commentService.updateComment(1, dto, otherUser.getId()));
    }

    @Test
    public void deleteById_WithCommentOwner_Should_SoftDelete() {
        // Arrange
        User commentOwner = createMockUser();
        Post post = new Post();
        Comment comment = createComment(1, commentOwner, post);

        Mockito.when(mockUserRepository.findById(commentOwner.getId())).thenReturn(commentOwner);
        Mockito.when(mockCommentRepository.findById(1)).thenReturn(comment);
        Mockito.when(mockCommentRepository.save(comment)).thenReturn(comment);

        // Act
        commentService.deleteById(1, commentOwner.getId());

        // Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(comment);
        Assertions.assertTrue(comment.isDeleted());
        Assertions.assertNotNull(comment.getDeletedAt());
    }

    @Test
    public void deleteById_WithModerator_Should_Succeed() {
        // Arrange
        User commentOwner = createMockUser();
        commentOwner.setRole(Role.USER);
        User moderator = createMockUser();
        moderator.setId(2);
        moderator.setRole(Role.MODERATOR);

        Post post = new Post();
        Comment comment = createComment(1, commentOwner, post);

        Mockito.when(mockUserRepository.findById(moderator.getId())).thenReturn(moderator);
        Mockito.when(mockCommentRepository.findById(1)).thenReturn(comment);
        Mockito.when(mockCommentRepository.save(comment)).thenReturn(comment);

        // Act
        commentService.deleteById(1, moderator.getId());

        // Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(comment);
        Assertions.assertTrue(comment.isDeleted());
    }

    private Comment createComment(int id, User user, Post post) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent("Original content");
        comment.setCreatedAt(LocalDateTime.now().minusHours(1));
        comment.setUpdatedAt(LocalDateTime.now().minusHours(1));
        comment.setDeleted(false);
        return comment;
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setRole(Role.USER);
        return user;
    }
}