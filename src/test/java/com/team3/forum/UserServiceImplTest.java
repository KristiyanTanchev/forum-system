package com.team3.forum;

import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.repositories.UserRepository;
import com.team3.forum.services.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.team3.forum.UserHelpers.createMockUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepository mockUserRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void findAll_Should_Call_Repository() {
        //Arrange
        Mockito.when(mockUserRepository.findAll()).thenReturn(List.of());
        //Act
        userService.findAll();
        //Assert
        Mockito.verify(mockUserRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findAll_Should_Return_ListOfUsers() {
        //Arrange
        var user1 = createMockUser();
        var user2 = createMockUser();
        user2.setId(2);
        List<User> expectedUsers = List.of(user1, user2);
        Mockito.when(mockUserRepository.findAll()).thenReturn(expectedUsers);
        //Act
        List<User> result = userService.findAll();
        //Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(expectedUsers, result);
    }

    @Test
    public void findById_Should_Call_Repository() {
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(mockUser);
        //Act
        User result = userService.findById(mockUser.getId());
        //Assert
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(mockUser.getId());
        Assertions.assertEquals(result, mockUser);
    }

    @Test
    public void findById_Should_Return_CorrectUser() {
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act
        User result = userService.findById(1);
        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("john_doe", result.getUsername());
    }

    @Test
    public void existsById_Should_Return_True_When_UserExists() {
        //Arrange
        Mockito.when(mockUserRepository.existsById(1)).thenReturn(true);
        //Act
        boolean result = userService.existsById(1);
        //Assert
        Assertions.assertTrue(result);
        Mockito.verify(mockUserRepository, Mockito.times(1)).existsById(1);
    }

    @Test
    public void existsById_Should_Return_False_When_UserDoesntExists() {
        //Arrange
        Mockito.when(mockUserRepository.existsById(999)).thenReturn(false);
        //Act
        boolean result = userService.existsById(999);
        //Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void findByUsername_Should_Call_Repository() {
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findByUsername(mockUser.getUsername())).thenReturn(mockUser);
        //Act
        User result = userService.findByUsername(mockUser.getUsername());
        //Assert
        Mockito.verify(mockUserRepository, Mockito.times(1)).findByUsername(mockUser.getUsername());
        Assertions.assertEquals(result, mockUser);
    }

    @Test
    public void blockUser_Should_BlockUser_Successfully() {
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
        //Act
        User result = userService.blockUser(mockUser.getId());
        //Assert
        Assertions.assertTrue(result.isBlocked());
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    public void blockUser_Should_Throw_When_UserNotFound() {
        //Arrange
        Mockito.when(mockUserRepository.findById(999))
                .thenThrow(new EntityNotFoundException("User", 999));
        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.blockUser(999));
    }

    @Test
    public void blockUser_Should_Throw_When_UserIsAlreadyBlocked() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setBlocked(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.blockUser(1));
    }

    @Test
    public void blockUser_Should_Throw_When_UserIsAlreadyDeleted() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setDeleted(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.blockUser(1));
    }

    @Test
    public void unblockUser_Should_UnblockUser_Successfully() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setBlocked(true);
        Mockito.when(mockUserRepository.findById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
        //Act
        User result = userService.unblockUser(mockUser.getId());
        //Assert
        Assertions.assertFalse(result.isBlocked());
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(mockUser);
    }



}
