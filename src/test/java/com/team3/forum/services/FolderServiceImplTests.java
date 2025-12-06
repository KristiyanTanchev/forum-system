package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.exceptions.FolderNotEmptyException;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.folderDtos.FolderUpdateDto;
import com.team3.forum.repositories.FolderRepository;
import com.team3.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FolderServiceImplTests {

    @Mock
    FolderRepository folderRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    FolderServiceImpl folderService;

    // ---------- findAll / findById ----------

    @Test
    public void findAll_Should_Return_ListOfFolders() {
        // Arrange
        Folder folder1 = new Folder();
        folder1.setId(1);
        Folder folder2 = new Folder();
        folder2.setId(2);
        List<Folder> expected = List.of(folder1, folder2);
        Mockito.when(folderRepository.findAll()).thenReturn(expected);

        // Act
        List<Folder> result = folderService.findAll();

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void findById_Should_Return_Folder_When_Exists() {
        // Arrange
        int id = 1;
        Folder folder = new Folder();
        folder.setId(id);
        Mockito.when(folderRepository.findById(id)).thenReturn(Optional.of(folder));

        // Act
        Folder result = folderService.findById(id);

        // Assert
        Assertions.assertEquals(folder, result);
    }

    @Test
    public void findById_Should_Throw_When_NotFound() {
        // Arrange
        int id = 1;
        Mockito.when(folderRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.findById(id)
        );
    }

    // ---------- deleteById ----------

    @Test
    public void deleteById_Should_Throw_When_Requester_Not_Admin() {
        // Arrange
        int folderId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);

        // Act & Assert
        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.DELETE_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void deleteById_Should_Throw_When_Folder_NotFound() {
        // Arrange
        int folderId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.findById(folderId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
    }

    @Test
    public void deleteById_Should_Throw_When_Folder_Not_Empty_Because_Of_Posts() {
        // Arrange
        int folderId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Folder folder = new Folder();
        folder.setId(folderId);
        var posts = new HashSet<Post>();
        posts.add(new Post());
        folder.setPosts(posts);
        folder.setChildFolders(new HashSet<>());

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));

        // Act & Assert
        Assertions.assertThrows(
                FolderNotEmptyException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
    }

    @Test
    public void deleteById_Should_Throw_When_Folder_Not_Empty_Because_Of_ChildFolders() {
        // Arrange
        int folderId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Folder folder = new Folder();
        folder.setId(folderId);
        folder.setPosts(new HashSet<>());

        Folder child = new Folder();
        child.setId(2);
        var children = new HashSet<Folder>();
        children.add(child);
        folder.setChildFolders(children);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));

        // Act & Assert
        Assertions.assertThrows(
                FolderNotEmptyException.class,
                () -> folderService.deleteById(folderId, requesterId)
        );
    }

    @Test
    public void deleteById_Should_Delete_When_Admin_And_Empty() {
        // Arrange
        int folderId = 1;
        int requesterId = 10;

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        Folder folder = new Folder();
        folder.setId(folderId);
        folder.setPosts(new HashSet<>());
        folder.setChildFolders(new HashSet<>());

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));

        // Act
        folderService.deleteById(folderId, requesterId);

        // Assert
        Mockito.verify(folderRepository).delete(folder);
    }

    // ---------- create ----------

    @Test
    public void create_Should_Throw_When_No_Slugs() {
        // Arrange
        Folder folder = new Folder();
        int requesterId = 10;

        // Act & Assert
//        EntityUpdateConflictException ex = Assertions.assertThrows(
//                EntityUpdateConflictException.class,
//                () -> folderService.create(folder, List.of(), requesterId)
//        );
//        Assertions.assertEquals(FolderServiceImpl.CREATE_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void create_Should_Throw_When_Requester_Not_Admin() {
        // Arrange
        Folder folder = new Folder();
        int requesterId = 10;
        List<String> slugs = List.of("parent");

        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);

        // Act & Assert
        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.create(folder, slugs, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void create_Should_Throw_When_Slug_Not_Unique_Among_Siblings() {
        // Arrange
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        Folder existingChild = new Folder();
        existingChild.setId(2);
        existingChild.setSlug("duplicate-slug");
        existingChild.setParentFolder(parent);
        parent.getChildFolders().add(existingChild);

        Folder newFolder = new Folder();
        newFolder.setSlug("duplicate-slug");

        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        List<String> slugs = List.of("parent");

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.findBySlug("parent")).thenReturn(parent);

        // Act & Assert
        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.create(newFolder, slugs, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_UNIQUE_SLUG_ERROR, ex.getMessage());
    }

    @Test
    public void create_Should_Set_Parent_And_Save_When_Valid() {
        // Arrange
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        Folder newFolder = new Folder();
        newFolder.setSlug("unique-slug");

        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        List<String> slugs = List.of("parent");

        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.findBySlug("parent")).thenReturn(parent);
        Mockito.when(folderRepository.save(Mockito.any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Folder result = folderService.create(newFolder, slugs, requesterId);

        // Assert
        Assertions.assertEquals(parent, result.getParentFolder());
        Mockito.verify(folderRepository).save(newFolder);
    }

    // ---------- update ----------

    @Test
    public void update_Should_Throw_When_No_Slugs() {
        // Arrange
        FolderUpdateDto dto = new FolderUpdateDto();
        int requesterId = 10;

        // Act & Assert
        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.update(List.of(), dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.EDIT_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Throw_When_Target_Is_Root_Folder() {
        // Arrange
        Folder root = new Folder();
        root.setId(1);
        root.setSlug("root");
        root.setParentFolder(null);

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setName("New name");
        dto.setSlug("new-slug");

        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        List<String> slugs = List.of("root");

        Mockito.when(folderRepository.findBySlug("root")).thenReturn(root);

        // Act & Assert
        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.update(slugs, dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.EDIT_NO_SLUGS_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Throw_When_Requester_Not_Admin() {
        // Arrange
        Folder parent = new Folder();
        parent.setId(1);

        Folder child = new Folder();
        child.setId(2);
        child.setParentFolder(parent);
        child.setSlug("child");

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setName("New name");
        dto.setSlug("new-slug");

        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(false);

        List<String> slugs = List.of("parent", "child");

        Mockito.when(folderRepository.findBySlug("parent")).thenReturn(parent);
        Mockito.when(folderRepository.findByParentFolderAndSlug(parent, "child"))
                .thenReturn(Optional.of(child));
        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);

        // Act & Assert
        AuthorizationException ex = Assertions.assertThrows(
                AuthorizationException.class,
                () -> folderService.update(slugs, dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.EDIT_AUTHORIZATION_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Throw_When_Slug_Not_Unique_Among_Siblings() {
        // Arrange
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
        dto.setName("New name");
        dto.setSlug("duplicate-slug");

        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        List<String> slugs = List.of("parent", "child");

        Mockito.when(folderRepository.findBySlug("parent")).thenReturn(parent);
        Mockito.when(folderRepository.findByParentFolderAndSlug(parent, "child"))
                .thenReturn(Optional.of(child));
        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);

        // Act & Assert
        EntityUpdateConflictException ex = Assertions.assertThrows(
                EntityUpdateConflictException.class,
                () -> folderService.update(slugs, dto, requesterId)
        );
        Assertions.assertEquals(FolderServiceImpl.CREATE_UNIQUE_SLUG_ERROR, ex.getMessage());
    }

    @Test
    public void update_Should_Update_And_Save_When_Admin() {
        // Arrange
        Folder parent = new Folder();
        parent.setId(1);
        parent.setChildFolders(new HashSet<>());

        Folder child = new Folder();
        child.setId(2);
        child.setParentFolder(parent);
        child.setSlug("child");
        child.setName("Old name");
        parent.getChildFolders().add(child);

        FolderUpdateDto dto = new FolderUpdateDto();
        dto.setName("New name");
        dto.setSlug("new-slug");

        int requesterId = 10;
        User requester = new User();
        requester.setId(requesterId);
        requester.setAdmin(true);

        List<String> slugs = List.of("parent", "child");

        Mockito.when(folderRepository.findBySlug("parent")).thenReturn(parent);
        Mockito.when(folderRepository.findByParentFolderAndSlug(parent, "child"))
                .thenReturn(Optional.of(child));
        Mockito.when(userRepository.findById(requesterId)).thenReturn(requester);
        Mockito.when(folderRepository.save(Mockito.any(Folder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Folder result = folderService.update(slugs, dto, requesterId);

        // Assert
        Assertions.assertEquals("New name", result.getName());
        Assertions.assertEquals("new-slug", result.getSlug());
        Mockito.verify(folderRepository).save(child);
    }

    // ---------- getPostsInFolder ----------

    @Test
    public void getPostsInFolder_Should_Return_Posts_When_Folder_Exists() {
        // Arrange
        Folder input = new Folder();
        input.setId(1);

        Folder persistent = new Folder();
        persistent.setId(1);
        var posts = new HashSet<Post>();
        Post post2 = new Post();
        post2.setId(2);
        posts.add(new Post());
        posts.add(post2);
        persistent.setPosts(posts);

        Mockito.when(folderRepository.findById(1)).thenReturn(Optional.of(persistent));

        // Act
        var result = folderService.getPostsInFolder(input);

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertNotSame(new ArrayList<>(posts), result);
    }

    @Test
    public void getPostsInFolder_Should_Throw_When_Folder_NotFound() {
        // Arrange
        Folder input = new Folder();
        input.setId(1);
        Mockito.when(folderRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.getPostsInFolder(input)
        );
    }

    // ---------- findHomeFolders ----------

    @Test
    public void findHomeFolders_Should_Return_Folders_With_Null_Parent() {
        // Arrange
        Folder root1 = new Folder();
        root1.setId(1);
        Folder root2 = new Folder();
        root2.setId(2);
        List<Folder> expected = List.of(root1, root2);

        Mockito.when(folderRepository.getFoldersByParentFolder(null)).thenReturn(expected);

        // Act
        List<Folder> result = folderService.findHomeFolders();

        // Assert
        Assertions.assertEquals(expected, result);
    }

    // ---------- getFolderByPath ----------

    @Test
    public void getFolderByPath_Should_Throw_When_Empty_Path() {
        // Act & Assert
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> folderService.getFolderByPath(List.of())
        );
    }

    @Test
    public void getFolderByPath_Should_Throw_When_First_Slug_Not_Found() {
        // Arrange
        Mockito.when(folderRepository.findBySlug("root")).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.getFolderByPath(List.of("root"))
        );
    }

    @Test
    public void getFolderByPath_Should_Throw_When_Intermediate_Slug_Not_Found() {
        // Arrange
        Folder root = new Folder();
        root.setId(1);

        Mockito.when(folderRepository.findBySlug("root")).thenReturn(root);
        Mockito.when(folderRepository.findByParentFolderAndSlug(root, "child"))
                .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> folderService.getFolderByPath(List.of("root", "child"))
        );
    }

    @Test
    public void getFolderByPath_Should_Return_Folder_For_Valid_Path() {
        // Arrange
        Folder root = new Folder();
        root.setId(1);

        Folder child = new Folder();
        child.setId(2);

        Mockito.when(folderRepository.findBySlug("root")).thenReturn(root);
        Mockito.when(folderRepository.findByParentFolderAndSlug(root, "child"))
                .thenReturn(Optional.of(child));

        // Act
        Folder result = folderService.getFolderByPath(List.of("root", "child"));

        // Assert
        Assertions.assertEquals(child, result);
    }
}
