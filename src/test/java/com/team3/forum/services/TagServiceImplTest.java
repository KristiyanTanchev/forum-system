package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.models.Tag;
import com.team3.forum.models.User;
import com.team3.forum.repositories.TagRepository;
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
public class TagServiceImplTest {

    @Mock
    TagRepository mockTagRepository;

    @InjectMocks
    TagServiceImpl tagService;

    @Test
    public void findAll_Should_Call_Repository() {
        //Arrange
        Mockito.when(mockTagRepository.findAll()).thenReturn(List.of());
        //Act
        tagService.findAll();
        //Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findById_Should_Call_Repository() {
        //Arrange
        Tag mockTag = new Tag();
        mockTag.setId(1);
        mockTag.setName("test");
        Mockito.when(mockTagRepository.findById(Mockito.anyInt())).thenReturn(mockTag);
        //Act
        Tag result = tagService.findById(mockTag.getId());
        //Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).findById(1);
        Assertions.assertEquals(result, mockTag);
    }

    @Test
    public void create_WithAdminUser_Should_Call_Repository() {
        //Arrange
        User adminUser = createMockAdminUser();
        Tag tag = new Tag();
        tag.setName("crypto");

        Mockito.when(mockTagRepository.findAll()).thenReturn(List.of());
        Mockito.when(mockTagRepository.save(Mockito.any(Tag.class))).thenReturn(tag);
        //Act
        Tag result = tagService.createTag(tag, adminUser);
        //Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).save(tag);
        Assertions.assertEquals("crypto", result.getName());
    }

    @Test
    public void create_WithRegularUser_Should_Throw_AuthorizationException() {
        //Arrange
        User regularUser = createMockUser();
        Tag tag = new Tag();
        //Act & Assert
        Assertions.assertThrows(AuthorizationException.class, () ->
                tagService.createTag(tag, regularUser));
    }

    @Test
    public void update_WithAdminUser_Should_Call_Repository() {
        //Arrange
        User adminUser = createMockAdminUser();
        Tag existingTag = new Tag();
        existingTag.setId(1);
        existingTag.setName("oldname");

        Tag updatedTag = new Tag();
        updatedTag.setName("newname");

        Mockito.when(mockTagRepository.findById(1)).thenReturn(existingTag);
        Mockito.when(mockTagRepository.save(existingTag)).thenReturn(existingTag);
        //Act
        Tag result = tagService.updateTag(1, updatedTag, adminUser);
        //Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).save(existingTag);
        Assertions.assertEquals("newname", result.getName());
    }

    @Test
    public void deleteById_WithAdminUser_Should_Call_Repository() {
        //Arrange
        User adminUser = createMockAdminUser();
        Mockito.when(mockTagRepository.existsById(1)).thenReturn(true);
        //Act
        tagService.deleteById(1, adminUser);
        //Assert
        Mockito.verify(mockTagRepository, Mockito.times(1)).deleteById(1);
    }

    private User createMockAdminUser() {
        User user = createMockUser();
        user.setAdmin(true);
        return user;
    }
}