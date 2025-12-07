package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.exceptions.FolderNotEmptyException;
import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.folderDtos.FolderCalculatedStatsDto;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.folderDtos.FolderUpdateDto;
import com.team3.forum.repositories.FolderRepository;
import com.team3.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolderServiceImplTests {

    @Mock
    FolderRepository folderRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    FolderMapper folderMapper;

    @InjectMocks
    FolderServiceImpl folderService;

    // ---------- findAll ----------

    @Test
    public void findAll_Should_Return_ListOfFolders() {
        // Arrange
        Folder f1 = new Folder();
        f1.setId(1);
        Folder f2 = new Folder();
        f2.setId(2);
        List<Folder> expected = List.of(f1, f2);
        when(folderRepository.findAll()).thenReturn(expected);

        // Act
        List<Folder> result = folderService.findAll();

        // Assert
        Assertions.assertEquals(expected, result);
    }

    // ---------- findById ----------

    @Test
    public void findById_Should_Return_Folder() {
        int id = 1;
        Folder folder = new Folder();
        folder.setId(id);
        when(folderRepository.findById(id)).thenReturn(folder);

        Folder result = folderService.findById(id);

        Assertions.assertEquals(folder, result);
    }

    @Test
    public void findById_Should_Propagate_Exception_When_NotFound() {
        int id = 1;
        when(folderRepository.findById(id))
                .thenThrow(new EntityNotFoundException("Folder not found"));

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.findById(id)
        );
    }

    // ---------- deleteById ----------

    @Test
    public void deleteById_Should_Throw_When_Requester_Not_Admin() {
        int folderId = 1;
        int requesterId = 10;

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(false);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.DELETE_AUTHORIZATION_ERROR, ex.getMessage());

        verify(folderRepository, never()).delete(any());
    }

    @Test
    public void deleteById_Should_Throw_When_Folder_Not_Empty_Posts() {
        int folderId = 1;
        int requesterId = 10;

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        Folder folder = new Folder();
        folder.setId(folderId);
        Set<Post> posts = new HashSet<>();
        posts.add(new Post());
        folder.setPosts(posts);
        folder.setChildFolders(new HashSet<>());

        when(folderRepository.findById(folderId)).thenReturn(folder);

        Assertions.assertThrows(
                FolderNotEmptyException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
        verify(folderRepository, never()).delete(any());
    }

    @Test
    public void deleteById_Should_Throw_When_Folder_Not_Empty_Children() {
        int folderId = 1;
        int requesterId = 10;

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        Folder folder = new Folder();
        folder.setId(folderId);
        folder.setPosts(new HashSet<>());
        Folder child = new Folder();
        child.setId(2);
        Set<Folder> children = new HashSet<>();
        children.add(child);
        folder.setChildFolders(children);

        when(folderRepository.findById(folderId)).thenReturn(folder);

        Assertions.assertThrows(
                FolderNotEmptyException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
        verify(folderRepository, never()).delete(any());
    }

    @Test
    public void deleteById_Should_Delete_When_Admin_And_Empty() {
        int folderId = 1;
        int requesterId = 10;

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        Folder folder = new Folder();
        folder.setId(folderId);
        folder.setPosts(new HashSet<>());
        folder.setChildFolders(new HashSet<>());

        when(folderRepository.findById(folderId)).thenReturn(folder);

        folderService.deleteById(folderId, requesterId);

        verify(folderRepository).delete(folder);
    }

    // ---------- create (with slugs) ----------

    @Test
    public void create_WithSlugs_Should_Throw_When_No_Slugs() {
        FolderCreateDto dto = new FolderCreateDto();
        int requesterId = 10;

        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.create(dto, List.of(), requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void create_WithSlugs_Should_Throw_When_Requester_Not_Moderator() {
        FolderCreateDto dto = new FolderCreateDto();
        dto.setParentFolderId(1);
        int requesterId = 10;

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(false);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        List<String> slugs = List.of("parent");

        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.create(dto, slugs, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void create_WithSlugs_Should_Throw_When_Slug_Not_Unique_Among_Siblings() {
        // parent
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        // existing child with same slug
        Folder existingChild = new Folder();
        existingChild.setId(2);
        existingChild.setSlug("duplicate-slug");
        existingChild.setParentFolder(parent);
        parent.getChildFolders().add(existingChild);

        int requesterId = 10;
        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        List<String> slugs = List.of("parent");
        when(folderRepository.findBySlug("parent")).thenReturn(parent);

        FolderCreateDto dto = new FolderCreateDto();
        dto.setParentFolderId(parent.getId());
        dto.setSlug("duplicate-slug");
        dto.setName("Any");

        Folder newFolder = new Folder();
        newFolder.setSlug("duplicate-slug");
        when(folderMapper.toEntity(dto)).thenReturn(newFolder);

        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.create(dto, slugs, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_UNIQUE_SLUG_ERROR, ex.getMessage());
    }

    @Test
    public void create_WithSlugs_Should_Set_Parent_And_Save_When_Valid() {
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        int requesterId = 10;
        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        List<String> slugs = List.of("parent");
        when(folderRepository.findBySlug("parent")).thenReturn(parent);

        FolderCreateDto dto = new FolderCreateDto();
        dto.setParentFolderId(parent.getId());
        dto.setSlug("unique-slug");
        dto.setName("New folder");

        Folder newFolder = new Folder();
        newFolder.setSlug("unique-slug");
        when(folderMapper.toEntity(dto)).thenReturn(newFolder);
        when(folderRepository.save(any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Folder result = folderService.create(dto, slugs, requesterId);

        Assertions.assertEquals(parent, result.getParentFolder());
        Assertions.assertEquals("unique-slug", result.getSlug());
        verify(folderRepository).save(newFolder);
    }

    // ---------- create (by parentId) ----------

    @Test
    public void create_ByParentId_Should_Throw_When_ParentId_Is_Zero() {
        FolderCreateDto dto = new FolderCreateDto();
        dto.setParentFolderId(0);
        int requesterId = 10;

        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.create(dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void create_ByParentId_Should_Throw_When_Requester_Not_Moderator() {
        FolderCreateDto dto = new FolderCreateDto();
        dto.setParentFolderId(1);
        int requesterId = 10;

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(false);
        when(userRepository.findById(requesterId)).thenReturn(requester);

        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.create(dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void create_ByParentId_Should_Set_Parent_And_Save_When_Valid() {
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        FolderCreateDto dto = new FolderCreateDto();
        dto.setParentFolderId(1);
        dto.setSlug("unique-slug");
        dto.setName("New folder");

        int requesterId = 10;
        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(requesterId)).thenReturn(requester);
        when(folderRepository.findById(1)).thenReturn(parent);

        Folder newFolder = new Folder();
        newFolder.setSlug("unique-slug");
        when(folderMapper.toEntity(dto)).thenReturn(newFolder);
        when(folderRepository.save(any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Folder result = folderService.create(dto, requesterId);

        Assertions.assertEquals(parent, result.getParentFolder());
        Assertions.assertEquals("unique-slug", result.getSlug());
        verify(folderRepository).save(newFolder);
    }

    // ---------- update (List<String> slugs wrapper) ----------

    @Test
    public void update_WithSlugs_Should_Throw_When_No_Slugs() {
        FolderUpdateDto dto = new FolderUpdateDto();
        int requesterId = 10;

        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.update(List.of(), dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.EDIT_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void update_WithSlugs_Should_Find_Folder_By_Path_And_Delegate() {
        Folder root = new Folder();
        root.setId(1);
        root.setParentFolder(null);

        Folder target = new Folder();
        target.setId(2);
        target.setParentFolder(root);

        List<String> slugs = List.of("root", "child");

        when(folderRepository.findBySlug("root")).thenReturn(root);
        when(folderRepository.findByParentFolderAndSlug(root, "child")).thenReturn(target);

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setName("New name");
        dto.setSlug("new-slug");
        dto.setDescription("desc");

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(10)).thenReturn(requester);

        when(folderRepository.findById(2)).thenReturn(target);
        when(folderRepository.save(any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Folder result = folderService.update(slugs, dto, 10);

        Assertions.assertEquals(2, dto.getId());
        Assertions.assertEquals("New name", result.getName());
        Assertions.assertEquals("new-slug", result.getSlug());
    }

    // ---------- update(FolderUpdateDto) core ----------

    @Test
    public void update_Should_Throw_When_Target_Is_Root_Folder() {
        Folder root = new Folder();
        root.setId(1);
        root.setParentFolder(null); // root

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setId(1);
        dto.setName("New name");
        dto.setSlug("new-slug");

        when(folderRepository.findById(1)).thenReturn(root);

        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.update(dto, 10)
        );
        Assertions.assertEquals(FolderServiceImpl.EDIT_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Throw_When_Requester_Not_Admin() {
        Folder parent = new Folder();
        parent.setId(1);

        Folder child = new Folder();
        child.setId(2);
        child.setParentFolder(parent);
        child.setSlug("child");

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setId(2);
        dto.setName("New name");
        dto.setSlug("new-slug");

        when(folderRepository.findById(2)).thenReturn(child);

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(false);
        when(userRepository.findById(10)).thenReturn(requester);

        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.update(dto, 10)
        );
        Assertions.assertEquals(FolderServiceImpl.EDIT_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Throw_When_Slug_Not_Unique_Among_Siblings() {
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        Folder child = new Folder();
        child.setId(2);
        child.setParentFolder(parent);
        child.setSlug("child");
        parent.getChildFolders().add(child);

        Folder sibling = new Folder();
        sibling.setId(3);
        sibling.setParentFolder(parent);
        sibling.setSlug("duplicate-slug");
        parent.getChildFolders().add(sibling);

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setId(2);
        dto.setName("New name");
        dto.setSlug("duplicate-slug");

        when(folderRepository.findById(2)).thenReturn(child);

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(10)).thenReturn(requester);

        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.update(dto, 10)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_UNIQUE_SLUG_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Update_And_Save_When_Admin_And_Valid() {
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        Folder child = new Folder();
        child.setId(2);
        child.setParentFolder(parent);
        child.setSlug("child");
        child.setName("Old name");
        child.setDescription("Old desc");
        parent.getChildFolders().add(child);

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setId(2);
        dto.setName("New name");
        dto.setSlug("new-slug");
        dto.setDescription("New desc");

        when(folderRepository.findById(2)).thenReturn(child);

        User requester = mock(User.class);
        when(requester.isModerator()).thenReturn(true);
        when(userRepository.findById(10)).thenReturn(requester);

        when(folderRepository.save(any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Folder result = folderService.update(dto, 10);

        Assertions.assertEquals("New name", result.getName());
        Assertions.assertEquals("new-slug", result.getSlug());
        Assertions.assertEquals("New desc", result.getDescription());
        verify(folderRepository).save(child);
    }

    // ---------- getPostsInFolder ----------

    @Test
    public void getPostsInFolder_Should_Return_Posts() {
        Folder input = new Folder();
        input.setId(1);

        Folder persistent = new Folder();
        persistent.setId(1);
        Set<Post> posts = new HashSet<>();
        posts.add(new Post());
        Post post2 = new Post();
        post2.setId(2);
        posts.add(post2);
        persistent.setPosts(posts);

        when(folderRepository.findById(1)).thenReturn(persistent);

        List<Post> result = folderService.getPostsInFolder(input);

        Assertions.assertEquals(2, result.size());
    }

    // ---------- findHomeFolders ----------

    @Test
    public void findHomeFolders_Should_Return_Folders_With_Null_Parent() {
        Folder root1 = new Folder();
        root1.setId(1);
        Folder root2 = new Folder();
        root2.setId(2);

        List<Folder> expected = List.of(root1, root2);
        when(folderRepository.getFoldersByParentFolder(null)).thenReturn(expected);

        List<Folder> result = folderService.findHomeFolders();

        Assertions.assertEquals(expected, result);
    }

    // ---------- getFolderByPath ----------

    @Test
    public void getFolderByPath_Should_Throw_When_Empty_Path() {
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.getFolderByPath(List.of())
        );
    }

    @Test
    public void getFolderByPath_Should_Throw_When_First_Slug_Not_Root() {
        Folder notRoot = new Folder();
        notRoot.setId(1);
        notRoot.setParentFolder(new Folder()); // not root

        when(folderRepository.findBySlug("root")).thenReturn(notRoot);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.getFolderByPath(List.of("root"))
        );
    }

    @Test
    public void getFolderByPath_Should_Throw_When_Intermediate_Slug_Not_Found() {
        Folder root = new Folder();
        root.setId(1);
        root.setParentFolder(null);

        when(folderRepository.findBySlug("root")).thenReturn(root);
        when(folderRepository.findByParentFolderAndSlug(root, "child"))
                .thenThrow(new EntityNotFoundException("Not found"));

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.getFolderByPath(List.of("root", "child"))
        );
    }

    @Test
    public void getFolderByPath_Should_Return_Folder_For_Valid_Path() {
        Folder root = new Folder();
        root.setId(1);
        root.setParentFolder(null);

        Folder child = new Folder();
        child.setId(2);
        child.setParentFolder(root);

        when(folderRepository.findBySlug("root")).thenReturn(root);
        when(folderRepository.findByParentFolderAndSlug(root, "child"))
                .thenReturn(child);

        Folder result = folderService.getFolderByPath(List.of("root", "child"));

        Assertions.assertEquals(child, result);
    }

    // ---------- getSiblingFolders ----------

    @Test
    public void getSiblingFolders_Should_Return_All_But_Self() {
        Folder parent = new Folder();
        parent.setId(1);

        Folder f1 = new Folder();
        f1.setId(1);
        f1.setParentFolder(parent);

        Folder f2 = new Folder();
        f2.setId(2);
        f2.setParentFolder(parent);

        Folder f3 = new Folder();
        f3.setId(3);
        f3.setParentFolder(parent);

        when(folderRepository.getFoldersByParentFolder(parent))
                .thenReturn(new ArrayList<>(List.of(f1, f2, f3)));

        List<Folder> siblings = folderService.getSiblingFolders(f2);

        Assertions.assertEquals(2, siblings.size());
        Assertions.assertFalse(siblings.contains(f2));
        Assertions.assertTrue(siblings.contains(f1));
        Assertions.assertTrue(siblings.contains(f3));
    }

    // ---------- getLastActivity ----------

    @Test
    public void getLastActivity_Should_Return_LastPost_When_Only_Posts() {
        Folder folder = new Folder();
        folder.setId(1);

        Folder persistent = new Folder();
        persistent.setId(1);

        LocalDateTime postTime = LocalDateTime.now().minusDays(1);

        when(folderRepository.findById(1)).thenReturn(persistent);
        when(folderRepository.getLastPostDate(persistent)).thenReturn(postTime);
        when(folderRepository.getLastCommentDate(persistent)).thenReturn(null);

        LocalDateTime result = folderService.getLastActivity(folder);

        Assertions.assertEquals(postTime, result);
    }

    @Test
    public void getLastActivity_Should_Return_LastComment_When_Only_Comments() {
        Folder folder = new Folder();
        folder.setId(1);

        Folder persistent = new Folder();
        persistent.setId(1);

        LocalDateTime commentTime = LocalDateTime.now().minusDays(2);

        when(folderRepository.findById(1)).thenReturn(persistent);
        when(folderRepository.getLastPostDate(persistent)).thenReturn(null);
        when(folderRepository.getLastCommentDate(persistent)).thenReturn(commentTime);

        LocalDateTime result = folderService.getLastActivity(folder);

        Assertions.assertEquals(commentTime, result);
    }

    @Test
    public void getLastActivity_Should_Return_Most_Recent_Of_Post_And_Comment() {
        Folder folder = new Folder();
        folder.setId(1);

        Folder persistent = new Folder();
        persistent.setId(1);

        LocalDateTime postTime = LocalDateTime.now().minusDays(2);
        LocalDateTime commentTime = LocalDateTime.now().minusDays(1);

        when(folderRepository.findById(1)).thenReturn(persistent);
        when(folderRepository.getLastPostDate(persistent)).thenReturn(postTime);
        when(folderRepository.getLastCommentDate(persistent)).thenReturn(commentTime);

        LocalDateTime result = folderService.getLastActivity(folder);

        Assertions.assertEquals(commentTime, result);
    }

    // ---------- buildSlugPath ----------

    @Test
    public void buildSlugPath_Should_Return_Slugs_From_Root_To_Current() {
        Folder root = new Folder();
        root.setSlug("root");
        root.setParentFolder(null);

        Folder movies = new Folder();
        movies.setSlug("movies");
        movies.setParentFolder(root);

        Folder action = new Folder();
        action.setSlug("action");
        action.setParentFolder(movies);

        List<String> slugs = folderService.buildSlugPath(action);

        Assertions.assertEquals(List.of("root", "movies", "action"), slugs);
    }

    // ---------- buildFolderResponseDto (and stats) ----------

    @Test
    public void buildFolderResponseDto_Should_Use_Mapper_With_Calculated_Stats() {
        Folder root = new Folder();
        root.setSlug("root");
        root.setParentFolder(null);

        Folder movies = new Folder();
        movies.setSlug("movies");
        movies.setParentFolder(root);
        root.setChildFolders(new HashSet<>(Collections.singleton(movies)));

        Folder action = new Folder();
        action.setId(3);
        action.setSlug("action");
        action.setName("Action");
        action.setParentFolder(movies);

        Post p1 = new Post();
        Post p2 = new Post();
        p2.setId(2);
        action.setPosts(new HashSet<>(Arrays.asList(p1, p2)));
        action.setChildFolders(new HashSet<>());

        when(folderRepository.findById(3)).thenReturn(action);
        when(folderRepository.getLastPostDate(action)).thenReturn(null);
        when(folderRepository.getLastCommentDate(action)).thenReturn(null);

        FolderResponseDto dto = new FolderResponseDto();
        when(folderMapper.toResponseDto(eq(action), any(FolderCalculatedStatsDto.class)))
                .thenReturn(dto);

        FolderResponseDto result = folderService.buildFolderResponseDto(action);

        Assertions.assertEquals(dto, result);

        ArgumentCaptor<FolderCalculatedStatsDto> statsCaptor =
                ArgumentCaptor.forClass(FolderCalculatedStatsDto.class);
        verify(folderMapper).toResponseDto(eq(action), statsCaptor.capture());

        FolderCalculatedStatsDto stats = statsCaptor.getValue();
        // lastActivity is empty string when no activity
        Assertions.assertEquals("", stats.getLastActivity());
        // path should be root/movies/action
        Assertions.assertEquals("root/movies/action", stats.getPath());
        // postCount should be number of posts in this folder
        Assertions.assertEquals(2, stats.getPostCount());
        // postCountWithSubfolders should be same here (no children)
        Assertions.assertEquals(2, stats.getPostCountWithSubfolders());
    }
}
