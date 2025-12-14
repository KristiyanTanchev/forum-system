package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Tag;
import com.team3.forum.models.User;
import com.team3.forum.models.enums.Role;
import com.team3.forum.repositories.TagRepository;
import com.team3.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {

    @Mock
    TagRepository mockTagRepository;

    @Mock
    UserRepository mockUserRepository;

    @InjectMocks
    TagServiceImpl tagService;

    @Test
    public void findAll_Should_Call_Repository() {
        // Arrange
        Mockito.when(mockTagRepository.findAll()).thenReturn(List.of());

        // Act
        tagService.findAll();

        // Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findById_Should_Call_Repository() {
        // Arrange
        Tag mockTag = new Tag();
        mockTag.setId(1);
        mockTag.setName("test");
        Mockito.when(mockTagRepository.findById(anyInt())).thenReturn(mockTag);

        // Act
        Tag result = tagService.findById(mockTag.getId());

        // Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).findById(1);
        Assertions.assertEquals(result, mockTag);
    }

    @Test
    public void create_WithAdminUser_Should_Call_Repository() {
        // Arrange
        User adminUser = createMockAdminUser();
        String tagName = "crypto";

        Mockito.when(mockUserRepository.findById(adminUser.getId())).thenReturn(adminUser);
        Mockito.when(mockTagRepository.findAll()).thenReturn(List.of());
        Mockito.when(mockTagRepository.save(Mockito.any(Tag.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Tag result = tagService.createTag(tagName, adminUser.getId());

        // Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).save(Mockito.any(Tag.class));
        Assertions.assertEquals("crypto", result.getName().toLowerCase());
    }

    @Test
    public void create_WithRegularUser_Should_Throw_AuthorizationException() {
        // Arrange
        User regularUser = createMockUser();
        regularUser.setRole(Role.USER);
        String tagName = "crypto";

        Mockito.when(mockUserRepository.findById(regularUser.getId())).thenReturn(regularUser);

        // Act & Assert
        Assertions.assertThrows(AuthorizationException.class, () ->
                tagService.createTag(tagName, regularUser.getId()));
    }

    @Test
    public void create_WithDuplicateName_Should_Throw_DuplicateEntityException() {
        // Arrange
        User adminUser = createMockAdminUser();
        String tagName = "crypto";
        Tag existingTag = new Tag();
        existingTag.setName("crypto");

        Mockito.when(mockUserRepository.findById(adminUser.getId())).thenReturn(adminUser);
        Mockito.when(mockTagRepository.findAll()).thenReturn(List.of(existingTag));

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () ->
                tagService.createTag(tagName, adminUser.getId()));
    }

    @Test
    public void update_WithAdminUser_Should_Call_Repository() {
        // Arrange
        User adminUser = createMockAdminUser();
        Tag existingTag = new Tag();
        existingTag.setId(1);
        existingTag.setName("oldname");
        String newName = "newname";

        Mockito.when(mockUserRepository.findById(adminUser.getId())).thenReturn(adminUser);
        Mockito.when(mockTagRepository.findById(1)).thenReturn(existingTag);
        Mockito.when(mockTagRepository.save(existingTag)).thenReturn(existingTag);

        // Act
        Tag result = tagService.updateTag(1, newName, adminUser.getId());

        // Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).save(existingTag);
        Assertions.assertEquals("newname", result.getName().toLowerCase());
    }

    @Test
    public void deleteById_WithAdminUser_Should_Call_Repository() {
        // Arrange
        User adminUser = createMockAdminUser();

        Mockito.when(mockUserRepository.findById(adminUser.getId())).thenReturn(adminUser);
        Mockito.when(mockTagRepository.existsById(1)).thenReturn(true);

        // Act
        tagService.deleteById(1, adminUser.getId());

        // Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    public void deleteById_WithNonExistingTag_Should_Throw_EntityNotFoundException() {
        // Arrange
        User adminUser = createMockAdminUser();

        Mockito.when(mockUserRepository.findById(adminUser.getId())).thenReturn(adminUser);
        Mockito.when(mockTagRepository.existsById(1)).thenReturn(false);

        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                tagService.deleteById(1, adminUser.getId()));
    }

    private User createMockAdminUser() {
        User user = createMockUser();
        user.setId(1);
        user.setRole(Role.ADMIN);
        return user;
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setRole(Role.USER);
        return user;
    }
}