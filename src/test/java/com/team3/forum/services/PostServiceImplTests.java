package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.postDtos.PostUpdateDto;
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
import java.util.HashSet;
import java.util.List;

import static helpers.PostHelpers.createMockPost;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    PostServiceImpl postService;

    @Test
    public void findAll_Should_Return_ListOfPosts() {
        // Arrange
        var post1 = createMockPost();
        var post2 = createMockPost();
        post2.setId(2);
        List<Post> expectedPosts = List.of(post1, post2);
        Mockito.when(postRepository.findAll()).thenReturn(expectedPosts);

        // Act
        List<Post> result = postService.findAll();

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(expectedPosts, result);
    }

    @Test
    public void findById_Should_Return_Post() {
        // Arrange
        Post post = createMockPost();
        post.setId(1);
        Mockito.when(postRepository.findById(1)).thenReturn(post);

        // Act
        Post result = postService.findById(1);

        // Assert
        Assertions.assertEquals(post, result);
    }

    @Test
    public void deleteById_Should_Throw_When_Post_NotFound() {
        // Arrange
        int postId = 1;
        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.deleteById(postId, requesterId)
        );
    }

    @Test
    public void deleteById_Should_Throw_When_Requester_Not_Admin_Or_Owner() {
        // Arrange
        int postId = 1;
        int ownerId = 2;
        int requesterId = 3;

        User owner = new User();
        owner.setId(ownerId);

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);
        post.setDeleted(false);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(post);

        // Act & Assert
        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> postService.deleteById(postId, requesterId)
        );
        Assertions.assertEquals(PostServiceImpl.DELETE_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void deleteById_Should_Mark_Deleted_And_Save_When_Admin() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Post post = new Post();
        post.setId(postId);
        post.setUser(new User()); // not important for admin branch
        post.setDeleted(false);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(post);

        // Act
        postService.deleteById(postId, requesterId);

        // Assert
        Assertions.assertTrue(post.isDeleted());
        Assertions.assertNotNull(post.getDeletedAt());
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void deleteById_Should_Mark_Deleted_And_Save_When_Owner() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Post post = new Post();
        post.setId(postId);
        post.setUser(requester);
        post.setDeleted(false);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(post);

        // Act
        postService.deleteById(postId, requesterId);

        // Assert
        Assertions.assertTrue(post.isDeleted());
        Assertions.assertNotNull(post.getDeletedAt());
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void restoreById_Should_Throw_When_Post_NotFound() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findByAndIsDeleted(postId)).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.restoreById(postId, requesterId)
        );
    }

    @Test
    public void restoreById_Should_Throw_When_Requester_Not_Admin_Or_Owner() {
        // Arrange
        int postId = 1;
        int ownerId = 2;
        int requesterId = 3;

        User owner = new User();
        owner.setId(ownerId);

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);
        post.setDeleted(true);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findByAndIsDeleted(postId)).thenReturn(post);

        // Act & Assert
        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> postService.restoreById(postId, requesterId)
        );
        Assertions.assertEquals(PostServiceImpl.RESTORE_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void restoreById_Should_Restore_Post_When_Admin() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Post post = new Post();
        post.setId(postId);
        post.setUser(new User());
        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findByAndIsDeleted(postId)).thenReturn(post);
        Mockito.when(postRepository.save(Mockito.any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.restoreById(postId, requesterId);

        // Assert
        Assertions.assertFalse(post.isDeleted());
        Assertions.assertNull(post.getDeletedAt());
        Assertions.assertEquals(post, result);
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void restoreById_Should_Restore_Post_When_Owner() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Post post = new Post();
        post.setId(postId);
        post.setUser(requester);
        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findByAndIsDeleted(postId)).thenReturn(post);
        Mockito.when(postRepository.save(Mockito.any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.restoreById(postId, requesterId);

        // Assert
        Assertions.assertFalse(post.isDeleted());
        Assertions.assertNull(post.getDeletedAt());
        Assertions.assertEquals(post, result);
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void create_Should_Save_And_Return_Post() {
        // Arrange
        Post newPost = createMockPost();
        newPost.setId(0);
        Mockito.when(postRepository.save(newPost)).thenReturn(newPost);
//
//        // Act
//        Post result = postService.create(newPost);
//
//        // Assert
//        Assertions.assertEquals(newPost, result);
//        Mockito.verify(postRepository).save(newPost);
    }

    @Test
    public void update_Should_Throw_When_Requester_Not_Admin_Or_Owner() {
        // Arrange
        int postId = 1;
        int ownerId = 2;
        int requesterId = 3;

        User owner = new User();
        owner.setId(ownerId);

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("New title");
        dto.setContent("New content");

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(post);

        // Act & Assert
        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> postService.update(postId, dto, requesterId)
        );
        Assertions.assertEquals(PostServiceImpl.EDIT_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Update_Title_And_Content_When_Admin() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Post post = new Post();
        post.setId(postId);
        post.setUser(new User());
        post.setTitle("Old title");
        post.setContent("Old content");

        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("New title");
        dto.setContent("New content");

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(post);
        Mockito.when(postRepository.save(Mockito.any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.update(postId, dto, requesterId);

        // Assert
        Assertions.assertEquals("New title", post.getTitle());
        Assertions.assertEquals("New content", post.getContent());
        Assertions.assertEquals(post, result);
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void update_Should_Update_Title_And_Content_When_Owner() {
        // Arrange
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Post post = new Post();
        post.setId(postId);
        post.setUser(requester);
        post.setTitle("Old title");
        post.setContent("Old content");

        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("New title");
        dto.setContent("New content");

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(postRepository.findById(postId)).thenReturn(post);
        Mockito.when(postRepository.save(Mockito.any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.update(postId, dto, requesterId);

        // Assert
        Assertions.assertEquals("New title", post.getTitle());
        Assertions.assertEquals("New content", post.getContent());
        Assertions.assertEquals(post, result);
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void getLikes_Should_Return_Size_Of_LikedBy() {
        // Arrange
        int postId = 1;
        Post post = new Post();
        post.setId(postId);
        post.getLikedBy().add(new User());

        Mockito.when(postRepository.findById(postId)).thenReturn(post);

        // Act
        int likes = postService.getLikes(postId);

        // Assert
        Assertions.assertEquals(1, likes);
    }

    @Test
    public void likePost_Should_Throw_When_User_Already_Liked() {
        // Arrange
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        post.getLikedBy().add(user);

        Mockito.when(postRepository.findById(postId)).thenReturn(post);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);

        // Act & Assert
        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> postService.likePost(postId, userId)
        );
    }

    @Test
    public void likePost_Should_Add_Like_And_Save() {
        // Arrange
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);

        Mockito.when(postRepository.findById(postId)).thenReturn(post);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);

        // Act
        postService.likePost(postId, userId);

        // Assert
        Assertions.assertTrue(post.getLikedBy().contains(user));
        Assertions.assertTrue(user.getLikedPosts().contains(post));
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void unlikePost_Should_Throw_When_User_Has_Not_Liked() {
        // Arrange
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        // user is NOT in likedBy

        Mockito.when(postRepository.findById(postId)).thenReturn(post);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.unlikePost(postId, userId)
        );
    }

    @Test
    public void unlikePost_Should_Remove_Like_And_Save() {
        // Arrange
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        post.getLikedBy().add(user);
        user.getLikedPosts().add(post);

        Mockito.when(postRepository.findById(postId)).thenReturn(post);
        Mockito.when(userRepository.findById(userId)).thenReturn(user);

        // Act
        postService.unlikePost(postId, userId);

        // Assert
        Assertions.assertFalse(post.getLikedBy().contains(user));
        Assertions.assertFalse(user.getLikedPosts().contains(post));
        Mockito.verify(postRepository).save(post);
    }
}
