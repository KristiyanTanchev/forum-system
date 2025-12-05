package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.helpers.UserMapper;
import com.team3.forum.models.User;
import com.team3.forum.models.enums.Role;
import com.team3.forum.models.userDtos.UserCreateDto;
import com.team3.forum.models.userDtos.UserUpdateDto;
import com.team3.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static helpers.UserHelpers.*;

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
    public void existsById_Should_Return_False_When_UserDoesNotExist() {
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

    @Test
    public void unblockUser_Should_Throw_When_UserNotFound() {
        //Arrange
        Mockito.when(mockUserRepository.findById(999))
                .thenThrow(new EntityNotFoundException("User", 999));
        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.unblockUser(999));
    }

    @Test
    public void unblockUser_Should_Throw_When_UserIsNotBlocked() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setBlocked(false);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.unblockUser(1));
    }

    @Test
    public void unblockUser_Should_Throw_When_UserIsAlreadyDeleted() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setBlocked(true);
        mockUser.setDeleted(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.unblockUser(1));
    }

    @Test
    public void promoteToAdmin_Should_Promote_User_Successfully() {
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
        //Act
        User result = userService.promoteToAdmin(mockUser.getId());
        //Assert
        Assertions.assertTrue(result.isAdmin());
        Assertions.assertEquals(Role.ADMIN, result.getRole());
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    public void promoteToAdmin_Should_Throw_When_UserNotFound() {
        //Arrange
        Mockito.when(mockUserRepository.findById(999))
                .thenThrow(new EntityNotFoundException("User", 999));
        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.promoteToAdmin(999));
    }

    @Test
    public void promoteToAdmin_Should_Throw_When_UserIsAlreadyAdmin() {
        var mockUser = createMockUser();
        mockUser.setRole(Role.ADMIN);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToAdmin(1));
    }


    @Test
    public void promoteToAdmin_Should_Throw_When_UserIsBlocked() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setBlocked(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToAdmin(1));
    }

    @Test
    public void promoteToAdmin_Should_Throw_When_UserIsDeleted() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setDeleted(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToAdmin(1));
    }

    @Test
    public void promoteToModerator_Should_Promote_User_Successfully() {
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findById(mockUser.getId())).thenReturn(mockUser);
        Mockito.when(mockUserRepository.save(mockUser)).thenReturn(mockUser);
        //Act
        User result = userService.promoteToModerator(mockUser.getId());
        //Assert
        Assertions.assertTrue(result.isModerator());
        Assertions.assertEquals(Role.MODERATOR, result.getRole());
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(mockUser);
    }

    @Test
    public void promoteToModerator_Should_Throw_When_UserNotFound() {
        //Arrange
        Mockito.when(mockUserRepository.findById(999))
                .thenThrow(new EntityNotFoundException("User", 999));
        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.promoteToModerator(999));
    }

    @Test
    public void promoteToModerator_Should_Throw_When_UserIsAlreadyModerator() {
        var mockUser = createMockModerator();
        Mockito.when(mockUserRepository.findById(mockUser.getId())).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToModerator(mockUser.getId()));
    }

    @Test
    public void promoteToModerator_Should_Throw_When_UserIsAlreadyAdmin() {
        var mockUser = createMockAdmin();
        Mockito.when(mockUserRepository.findById(mockUser.getId())).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToModerator(mockUser.getId()));
    }

    @Test
    public void promoteToModerator_Should_Throw_When_UserIsBlocked() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setBlocked(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToModerator(1));
    }

    @Test
    public void promoteToModerator_Should_Throw_When_UserIsDeleted() {
        //Arrange
        var mockUser = createMockUser();
        mockUser.setDeleted(true);
        Mockito.when(mockUserRepository.findById(1)).thenReturn(mockUser);
        //Act, Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () -> userService.promoteToModerator(1));
    }

    @Test
    public void searchUsers_Should_Call_Repository() {
        // Arrange
        String query = "john";
        List<User> expectedUsers = List.of(createMockUser());
        Mockito.when(mockUserRepository.searchUsers(query)).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.searchUsers(query);

        // Assert
        Assertions.assertEquals(expectedUsers, result);
        Mockito.verify(mockUserRepository, Mockito.times(1)).searchUsers(query);
    }

    @Test
    public void softDeleteById_Should_Call_Repository() {
        // Arrange
        Mockito.doNothing().when(mockUserRepository).softDeleteById(1);
        // Act
        userService.softDeleteById(1);
        // Assert
        Mockito.verify(mockUserRepository, Mockito.times(1)).softDeleteById(1);
    }

    @Test
    public void softDeleteById_Should_Throw_When_UserNotFound() {
        //Arrange
        Mockito.doThrow(new EntityNotFoundException("User", 999))
                .when(mockUserRepository).softDeleteById(999);
        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.softDeleteById(999));
    }

    @Test
    public void restoreById_Should_Call_Repository() {
        // Arrange
        Mockito.doNothing().when(mockUserRepository).restoreById(1);
        // Act
        userService.restoreById(1);
        // Assert
        Mockito.verify(mockUserRepository, Mockito.times(1)).restoreById(1);
    }

    @Test
    public void restoreById_Should_Throw_When_UserNotFound() {
        //Arrange
        Mockito.doThrow(new EntityNotFoundException("User", 999))
                .when(mockUserRepository).restoreById(999);
        //Act, Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.restoreById(999));
    }

    @Test
    public void createUser_Should_Create_User_Successfully() {
        //Arrange
        UserCreateDto dto = createMockUserCreateDto();
        User user = createMockUser();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        Mockito.when(mockUserRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        Mockito.when(mockUserRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(userMapper.toEntity(dto)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_password");
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(user);
        //Act
        User result = userService.createUser(dto);
        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Role.USER, result.getRole());
        Assertions.assertFalse(result.isAdmin());
        Assertions.assertFalse(result.isBlocked());
        Assertions.assertFalse(result.isDeleted());
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(dto.getPassword());
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void createUser_Should_Throw_When_UsernameExists() {
        //Arrange
        UserCreateDto dto = createMockUserCreateDto();
        Mockito.when(mockUserRepository.existsByUsername(dto.getUsername())).thenReturn(true);
        //Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.createUser(dto));
    }

    @Test
    public void createUser_Should_Throw_When_EmailExists() {
        //Arrange
        UserCreateDto dto = createMockUserCreateDto();
        Mockito.when(mockUserRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        Mockito.when(mockUserRepository.existsByEmail(dto.getEmail())).thenReturn(true);
        //Act, Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.createUser(dto));
    }

    @Test
    public void updateUser_Should_Update_Successfully_When_UserUpdatesOwnProfile() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        User existingUser = createMockUser();
        User requester = createMockUser();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser).thenReturn(requester);
        Mockito.when(mockUserRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(existingUser);
        // Act
        User result = userService.updateUser(1, dto, 1);
        // Assert
        Assertions.assertNotNull(result);
        Mockito.verify(userMapper, Mockito.times(1)).updateEntityFromDto(dto, existingUser);
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    public void updateUser_Should_Update_Successfully_When_AdminUpdatesOtherUser() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        User existingUser = createMockUser();
        User admin = createMockAdmin();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser);
        Mockito.when(mockUserRepository.findById(2)).thenReturn(admin);
        Mockito.when(mockUserRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(existingUser);
        // Act
        User result = userService.updateUser(1, dto, 2);
        // Assert
        Assertions.assertNotNull(result);
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    public void updateUser_Should_Throw_When_UserNotFound() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        Mockito.when(mockUserRepository.findById(999))
                .thenThrow(new EntityNotFoundException("User", 999));
        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(999, dto, 1));
    }

    @Test
    public void updateUser_Should_Throw_When_RequesterNotFound() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        User existingUser = createMockUser();
        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser);
        Mockito.when(mockUserRepository.findById(999))
                .thenThrow(new EntityNotFoundException("User", 999));
        // Act & Assert
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(1, dto, 999));
    }

    @Test
    public void updateUser_Should_Throw_When_NonAdminUpdatesOtherUser() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        User existingUser = createMockUser();
        User requester = createMockUser();
        requester.setId(2);
        requester.setRole(Role.USER);

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser);
        Mockito.when(mockUserRepository.findById(2)).thenReturn(requester);
        // Act & Assert
        Assertions.assertThrows(AuthorizationException.class, () ->
                userService.updateUser(1, dto, 2));
    }

    @Test
    public void updateUser_Should_Throw_When_UserIsBlocked() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        User existingUser = createMockUser();
        existingUser.setBlocked(true);
        User requester = createMockUser();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser).thenReturn(requester);

        // Act & Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () ->
                userService.updateUser(1, dto, 1));
    }

    @Test
    public void updateUser_Should_Throw_When_UserIsDeleted() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        User existingUser = createMockUser();
        existingUser.setBlocked(false);
        existingUser.setDeleted(true);
        User requester = createMockUser();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser).thenReturn(requester);

        // Act & Assert
        Assertions.assertThrows(EntityUpdateConflictException.class, () ->
                userService.updateUser(1, dto, 1));
    }

    @Test
    public void updateUser_Should_Throw_When_NewEmailAlreadyExists() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        dto.setEmail("existing@example.com");
        User existingUser = createMockUser();
        existingUser.setEmail("old@example.com");
        User requester = createMockUser();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser).thenReturn(requester);
        Mockito.when(mockUserRepository.existsByEmail("existing@example.com")).thenReturn(true);
        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () ->
                userService.updateUser(1, dto, 1));
    }
    @Test
    public void updateUser_Should_Not_Check_Email_When_EmailUnchanged() {
        // Arrange
        UserUpdateDto dto = createMockUserUpdateDto();
        dto.setEmail("john.doe@example.com");
        User existingUser = createMockUser();
        User requester = createMockUser();

        Mockito.when(mockUserRepository.findById(1)).thenReturn(existingUser, requester);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(existingUser);
        // Act
        userService.updateUser(1, dto, 1);
        // Assert
        Mockito.verify(mockUserRepository, Mockito.never()).existsByEmail(Mockito.anyString());
    }
}


