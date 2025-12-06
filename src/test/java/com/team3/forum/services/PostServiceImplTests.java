package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.helpers.PostMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.enums.PostSortField;
import com.team3.forum.models.enums.Role;
import com.team3.forum.models.enums.SortDirection;
import com.team3.forum.models.postDtos.PostCalculatedStatsDto;
import com.team3.forum.models.postDtos.PostCreationDto;
import com.team3.forum.models.postDtos.PostResponseDto;
import com.team3.forum.models.postDtos.PostUpdateDto;
import com.team3.forum.repositories.FolderRepository;
import com.team3.forum.repositories.PostRepository;
import com.team3.forum.repositories.PostViewRepository;
import com.team3.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    FolderRepository folderRepository;

    @Mock
    PostViewRepository postViewRepository;

    @Mock
    PostMapper postMapper;

    @InjectMocks
    PostServiceImpl postService;

    // ---------- findAll / findById ----------

    @Test
    public void findAll_Should_Return_ListOfPosts() {
        Post p1 = new Post();
        p1.setId(1);
        Post p2 = new Post();
        p2.setId(2);
        List<Post> expected = List.of(p1, p2);

        when(postRepository.findAll()).thenReturn(expected);

        List<Post> result = postService.findAll();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void findById_Should_Return_Post() {
        int id = 1;
        Post post = new Post();
        post.setId(id);
        when(postRepository.findById(id)).thenReturn(post);

        Post result = postService.findById(id);

        Assertions.assertEquals(post, result);
    }

    @Test
    public void findById_Should_Propagate_Exception_When_NotFound() {
        int id = 1;
        when(postRepository.findById(id))
                .thenThrow(new EntityNotFoundException("Post", id));

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.findById(id)
        );
    }

    // ---------- findByIdIncludeDeleted ----------

    @Test
    public void findByIdIncludeDeleted_Should_Return_Existing_Post_When_Not_Deleted() {
        int requesterId = 10;
        int postId = 1;

        User requester = new User();
        requester.setId(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        Post post = new Post();
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(post);

        Post result = postService.findByIdIncludeDeleted(postId, requesterId);

        Assertions.assertEquals(post, result);
        verify(postRepository, never()).findByAndIsDeleted(anyInt());
    }

    @Test
    public void findByIdIncludeDeleted_Should_Return_Deleted_Post_When_Requester_Is_Admin() {
        int requesterId = 10;
        int postId = 1;

        User admin = new User();
        admin.setId(requesterId);
        admin.setRole(Role.ADMIN);
        when(userRepository.findById(requesterId)).thenReturn(admin);

        when(postRepository.findById(postId))
                .thenThrow(new EntityNotFoundException("Post", postId));

        User author = new User();
        author.setId(20);

        Post deleted = new Post();
        deleted.setId(postId);
        deleted.setUser(author);

        when(postRepository.findByAndIsDeleted(postId)).thenReturn(deleted);

        Post result = postService.findByIdIncludeDeleted(postId, requesterId);

        Assertions.assertEquals(deleted, result);
    }

    @Test
    public void findByIdIncludeDeleted_Should_Return_Deleted_Post_When_Requester_Is_Owner() {
        int requesterId = 10;
        int postId = 1;

        User owner = new User();
        owner.setId(requesterId);
        owner.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(owner);

        when(postRepository.findById(postId))
                .thenThrow(new EntityNotFoundException("Post", postId));

        Post deleted = new Post();
        deleted.setId(postId);
        deleted.setUser(owner);

        when(postRepository.findByAndIsDeleted(postId)).thenReturn(deleted);

        Post result = postService.findByIdIncludeDeleted(postId, requesterId);

        Assertions.assertEquals(deleted, result);
    }

    @Test
    public void findByIdIncludeDeleted_Should_Throw_When_Deleted_And_Not_Admin_Or_Owner() {
        int requesterId = 10;
        int postId = 1;

        User requester = new User();
        requester.setId(requesterId);
        requester.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        EntityNotFoundException notFound = new EntityNotFoundException("Post", postId);
        when(postRepository.findById(postId)).thenThrow(notFound);

        User author = new User();
        author.setId(99);

        Post deleted = new Post();
        deleted.setId(postId);
        deleted.setUser(author);

        when(postRepository.findByAndIsDeleted(postId)).thenReturn(deleted);

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.findByIdIncludeDeleted(postId, requesterId)
        );
        Assertions.assertSame(notFound, ex);
    }

    // ---------- deleteById ----------

    @Test
    public void deleteById_Should_Throw_When_Post_NotFound() {
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        when(postRepository.findById(postId)).thenReturn(null);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.deleteById(postId, requesterId)
        );
    }

    @Test
    public void deleteById_Should_Throw_When_Requester_Not_Authorized() {
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        User author = new User();
        author.setId(20);
        author.setRole(Role.USER);

        Post post = new Post();
        post.setId(postId);
        post.setUser(author);

        when(postRepository.findById(postId)).thenReturn(post);

        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> postService.deleteById(postId, requesterId)
        );
        Assertions.assertEquals(PostServiceImpl.DELETE_AUTHORIZATION_ERROR, ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void deleteById_Should_SoftDelete_When_Owner() {
        int postId = 1;
        int requesterId = 10;

        User owner = new User();
        owner.setId(requesterId);
        owner.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(owner);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.deleteById(postId, requesterId);

        Assertions.assertTrue(post.isDeleted());
        Assertions.assertNotNull(post.getDeletedAt());
        verify(postRepository).save(post);
    }

    @Test
    public void deleteById_Should_Allow_Moderator_On_User_Post() {
        int postId = 1;
        int requesterId = 10;

        User moderator = new User();
        moderator.setId(requesterId);
        moderator.setRole(Role.MODERATOR);
        when(userRepository.findById(requesterId)).thenReturn(moderator);

        User author = new User();
        author.setId(20);
        author.setRole(Role.USER);

        Post post = new Post();
        post.setId(postId);
        post.setUser(author);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.deleteById(postId, requesterId);

        Assertions.assertTrue(post.isDeleted());
        verify(postRepository).save(post);
    }

    @Test
    public void deleteById_Should_Allow_Admin_On_Any_Post() {
        int postId = 1;
        int requesterId = 10;

        User admin = new User();
        admin.setId(requesterId);
        admin.setRole(Role.ADMIN);
        when(userRepository.findById(requesterId)).thenReturn(admin);

        User author = new User();
        author.setId(20);
        author.setRole(Role.ADMIN);

        Post post = new Post();
        post.setId(postId);
        post.setUser(author);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.deleteById(postId, requesterId);

        Assertions.assertTrue(post.isDeleted());
        verify(postRepository).save(post);
    }

    // ---------- restoreById ----------

    @Test
    public void restoreById_Should_Throw_When_Post_NotFound() {
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        when(postRepository.findByAndIsDeleted(postId)).thenReturn(null);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.restoreById(postId, requesterId)
        );
    }

    @Test
    public void restoreById_Should_Restore_When_Owner() {
        int postId = 1;
        int requesterId = 10;

        User owner = new User();
        owner.setId(requesterId);
        owner.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(owner);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);
        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());

        when(postRepository.findByAndIsDeleted(postId)).thenReturn(post);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.restoreById(postId, requesterId);

        Assertions.assertFalse(result.isDeleted());
        Assertions.assertNull(result.getDeletedAt());
        verify(postRepository).save(post);
    }

    // ---------- create ----------

    @Test
    public void create_Should_Map_And_Save() {
        int userId = 10;

        Folder folder = new Folder();
        folder.setId(5);

        User user = new User();
        user.setId(userId);

        PostCreationDto dto = new PostCreationDto();
        dto.setFolderId(folder.getId());
        dto.setTitle("Title");
        dto.setContent("Content");

        Post mapped = new Post();
        mapped.setTitle("Title");
        mapped.setContent("Content");

        when(postMapper.toEntity(dto)).thenReturn(mapped);
        when(folderRepository.findById(folder.getId())).thenReturn(folder);
        when(userRepository.findById(userId)).thenReturn(user);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.create(dto, userId);

        Assertions.assertEquals(folder, result.getFolder());
        Assertions.assertEquals(user, result.getUser());
        verify(postRepository).save(mapped);
    }

    // ---------- update ----------

    @Test
    public void update_Should_Throw_When_Not_Authorized() {
        int postId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        User author = new User();
        author.setId(20);
        author.setRole(Role.USER);

        Post post = new Post();
        post.setId(postId);
        post.setUser(author);

        when(postRepository.findById(postId)).thenReturn(post);

        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("New title");
        dto.setContent("New content");

        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> postService.update(postId, dto, requesterId)
        );
        Assertions.assertEquals(PostServiceImpl.EDIT_AUTHORIZATION_ERROR, ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void update_Should_Update_And_Save_When_Owner() {
        int postId = 1;
        int requesterId = 10;

        User owner = new User();
        owner.setId(requesterId);
        owner.setRole(Role.USER);
        when(userRepository.findById(requesterId)).thenReturn(owner);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);
        post.setTitle("Old");
        post.setContent("Old content");

        when(postRepository.findById(postId)).thenReturn(post);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostUpdateDto dto = new PostUpdateDto();
        dto.setTitle("New");
        dto.setContent("New content");

        Post result = postService.update(postId, dto, requesterId);

        Assertions.assertEquals("New", result.getTitle());
        Assertions.assertEquals("New content", result.getContent());
        verify(postRepository).save(post);
    }

    // ---------- likes ----------

    @Test
    public void getLikes_Should_Return_Size_Of_LikedBy() {
        Post post = new Post();
        post.setId(1);
        Set<User> likedBy = new HashSet<>();
        likedBy.add(User.builder().id(1).build());
        likedBy.add(User.builder().id(2).build());
        post.setLikedBy(likedBy);

        when(postRepository.findById(1)).thenReturn(post);

        int likes = postService.getLikes(1);

        Assertions.assertEquals(2, likes);
    }

    @Test
    public void likePost_Should_Throw_When_Already_Liked() {
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        Set<User> likedBy = new HashSet<>();
        likedBy.add(user);
        post.setLikedBy(likedBy);

        when(userRepository.findById(userId)).thenReturn(user);
        when(postRepository.findById(postId)).thenReturn(post);

        DuplicateEntityException ex = Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> postService.likePost(postId, userId)
        );
        Assertions.assertEquals(PostServiceImpl.ALREADY_LIKED_ERROR, ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void likePost_Should_Add_Like_And_Save() {
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        post.setLikedBy(new HashSet<>());

        when(userRepository.findById(userId)).thenReturn(user);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.likePost(postId, userId);

        Assertions.assertTrue(post.getLikedBy().contains(user));
        Assertions.assertTrue(user.getLikedPosts().contains(post));
        verify(postRepository).save(post);
    }

    @Test
    public void unlikePost_Should_Throw_When_Not_Liked() {
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        post.setLikedBy(new HashSet<>());

        when(userRepository.findById(userId)).thenReturn(user);
        when(postRepository.findById(postId)).thenReturn(post);

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> postService.unlikePost(postId, userId)
        );
        Assertions.assertEquals(PostServiceImpl.NOT_LIKED_ERROR, ex.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    public void unlikePost_Should_Remove_Like_And_Save() {
        int postId = 1;
        int userId = 10;

        User user = new User();
        user.setId(userId);
        user.setLikedPosts(new HashSet<>());

        Post post = new Post();
        post.setId(postId);
        Set<User> likedBy = new HashSet<>();
        likedBy.add(user);
        post.setLikedBy(likedBy);
        user.getLikedPosts().add(post);

        when(userRepository.findById(userId)).thenReturn(user);
        when(postRepository.findById(postId)).thenReturn(post);

        postService.unlikePost(postId, userId);

        Assertions.assertFalse(post.getLikedBy().contains(user));
        Assertions.assertFalse(user.getLikedPosts().contains(post));
        verify(postRepository).save(post);
    }

    // ---------- getPostsInFolderPaginated (simple) ----------

    @Test
    public void getPostsInFolderPaginated_Should_Call_Repository_With_Defaults_When_Invalid_Sort() {
        Folder folder = new Folder();
        folder.setId(5);

        List<Post> posts = List.of(new Post(), new Post());
        when(postRepository.findPostsInFolderPaginated(
                anyInt(),
                eq(PostServiceImpl.POSTS_PAGE_SIZE),
                eq(folder),
                eq(PostSortField.CREATED_AT),
                eq(SortDirection.DESC)
        )).thenReturn(posts);

        // invalid orderBy and direction
        List<Post> result = postService.getPostsInFolderPaginated(folder, 1, "blah", "up");

        Assertions.assertEquals(2, result.size());
        verify(postRepository).findPostsInFolderPaginated(
                1,
                PostServiceImpl.POSTS_PAGE_SIZE,
                folder,
                PostSortField.CREATED_AT,
                SortDirection.DESC
        );
    }

    @Test
    public void getPostsInFolderPaginated_Should_Correct_Negative_Page_To_One() {
        Folder folder = new Folder();
        folder.setId(5);

        when(postRepository.findPostsInFolderPaginated(
                anyInt(), anyInt(), any(), any(), any()
        )).thenReturn(Collections.emptyList());

        postService.getPostsInFolderPaginated(folder, -3, "CREATED_AT", "desc");

        verify(postRepository).findPostsInFolderPaginated(
                1,
                PostServiceImpl.POSTS_PAGE_SIZE,
                folder,
                PostSortField.CREATED_AT,
                SortDirection.DESC
        );
    }

    // ---------- trending & count ----------

    @Test
    public void getTrendingPosts_Should_Delegate_To_Repository() {
        List<Post> posts = List.of(new Post(), new Post());
        when(postRepository.findAllSortedByViewsLastDays(5, 7)).thenReturn(posts);

        List<Post> result = postService.getTrendingPosts();

        Assertions.assertEquals(posts, result);
        verify(postRepository).findAllSortedByViewsLastDays(5, 7);
    }

    @Test
    public void getPostsCount_Should_Delegate_To_Repository() {
        when(postRepository.getPostsCount()).thenReturn(42);

        int count = postService.getPostsCount();

        Assertions.assertEquals(42, count);
        verify(postRepository).getPostsCount();
    }

    // ---------- registerView / getPostViews ----------

    @Test
    public void registerView_Should_Register_When_Not_Exists_For_Today() {
        int postId = 1;
        int userId = 10;
        LocalDate today = LocalDate.now();

        when(postViewRepository.existsForDate(postId, userId, today)).thenReturn(false);

        postService.registerView(postId, userId);

        verify(postViewRepository).registerView(postId, userId);
    }

    @Test
    public void registerView_Should_Not_Register_When_Already_Exists() {
        int postId = 1;
        int userId = 10;
        LocalDate today = LocalDate.now();

        when(postViewRepository.existsForDate(postId, userId, today)).thenReturn(true);

        postService.registerView(postId, userId);

        verify(postViewRepository, never()).registerView(anyInt(), anyInt());
    }

    @Test
    public void getPostViews_Should_Return_Total_Views() {
        int postId = 1;
        when(postViewRepository.getTotalViewsForPost(postId)).thenReturn(123L);

        long views = postService.getPostViews(postId);

        Assertions.assertEquals(123L, views);
        verify(postViewRepository).getTotalViewsForPost(postId);
    }

    // ---------- buildPostResponseDto / calculated stats ----------

    @Test
    public void buildPostResponseDto_Should_Use_Mapper_With_Calculated_Stats() {
        // post and user
        User user = new User();
        user.setId(10);
        user.setUsername("john");
        user.setRole(Role.USER);
        user.setLikedPosts(new HashSet<>());

        Folder folder = new Folder();
        folder.setId(5);
        folder.setName("Movies");

        Post post = new Post();
        post.setId(1);
        post.setUser(user);
        post.setFolder(folder);
        post.setCreatedAt(LocalDateTime.now().minusDays(2));
        post.setUpdatedAt(LocalDateTime.now().minusDays(1));
        post.setDeletedAt(null);
        post.setComments(new HashSet<>());
        post.setLikedBy(new HashSet<>());
        post.setTags(new HashSet<>());

        // requester is same as user for findByIdIncludeDeleted
        when(userRepository.findById(user.getId())).thenReturn(user);
        when(postRepository.findById(post.getId())).thenReturn(post);
        when(postViewRepository.getTotalViewsForPost(post.getId())).thenReturn(5L);

        PostResponseDto responseDto = new PostResponseDto();
        when(postMapper.toResponseDto(eq(post), any(PostCalculatedStatsDto.class)))
                .thenReturn(responseDto);

        PostResponseDto result = postService.buildPostResponseDto(post);

        Assertions.assertEquals(responseDto, result);

        ArgumentCaptor<PostCalculatedStatsDto> statsCaptor =
                ArgumentCaptor.forClass(PostCalculatedStatsDto.class);
        verify(postMapper).toResponseDto(eq(post), statsCaptor.capture());

        PostCalculatedStatsDto stats = statsCaptor.getValue();
        Assertions.assertEquals("john", stats.getCreator());
        Assertions.assertEquals(user.getId(), stats.getUserId());
        Assertions.assertEquals("Movies", stats.getFolderName());
        Assertions.assertEquals(0, stats.getCommentsCount());
        Assertions.assertEquals(5L, stats.getViews());
    }
}
