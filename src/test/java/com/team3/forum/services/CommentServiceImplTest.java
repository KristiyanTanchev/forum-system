package com.team3.forum.services;

import com.team3.forum.models.Comment;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.commentDtos.CommentCreationDto;
import com.team3.forum.models.commentDtos.CommentUpdateDto;
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

import java.util.List;

import static helpers.UserHelpers.createMockUser;

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
        //Arrange
        Comment comment = createComment(1, createMockUser());
        comment.getPost().setId(1);

        Mockito.when(mockCommentRepository.findAll()).thenReturn(List.of(comment));
        //Act
        List<Comment> result = commentService.findAllByPostId(1);
        //Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).findAll();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findById_Should_Call_Repository() {
        //Arrange
        Comment mockComment = createComment(1, createMockUser());
        Mockito.when(mockCommentRepository.findById(Mockito.anyInt())).thenReturn(mockComment);
        //Act
        Comment result = commentService.findById(mockComment.getId());
        //Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).findById(1);
        Assertions.assertEquals(result, mockComment);
    }

    @Test
    public void create_WithValidData_Should_Call_Repository() {
        //Arrange
        User user = createMockUser();
        Post post = new Post();
        post.setId(1);
        CommentCreationDto dto = new CommentCreationDto();
        dto.setContent("Test comment");

        Mockito.when(mockPostRepository.findById(1)).thenReturn(post);
        Mockito.when(mockCommentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //Act
        Comment result = commentService.createComment(dto, 1, user.getId());
        //Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
        Assertions.assertEquals("Test comment", result.getContent());
    }

    @Test
    public void update_WithCommentOwner_Should_Call_Repository() {
        //Arrange
        User commentOwner = createMockUser();
        Comment existingComment = createComment(1, commentOwner);
        CommentUpdateDto dto = new CommentUpdateDto();
        dto.setContent("Updated content");

        Mockito.when(mockCommentRepository.findById(1)).thenReturn(existingComment);
        Mockito.when(mockCommentRepository.save(existingComment)).thenReturn(existingComment);
        //Act
        Comment result = commentService.updateComment(1, dto, commentOwner.getId());
        //Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(existingComment);
        Assertions.assertEquals("Updated content", result.getContent());
    }

    @Test
    public void deleteById_WithCommentOwner_Should_Call_Repository() {
        //Arrange
        User commentOwner = createMockUser();
        Comment comment = createComment(1, commentOwner);

        Mockito.when(mockCommentRepository.findById(1)).thenReturn(comment);
        //Act
        commentService.deleteById(1, commentOwner.getId());
        //Assert
        Mockito.verify(mockCommentRepository, Mockito.times(1)).delete(comment);
    }

    private Comment createComment(int id, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUser(user);
        comment.setPost(new Post());
        comment.setContent("Original content");
        return comment;
    }
}