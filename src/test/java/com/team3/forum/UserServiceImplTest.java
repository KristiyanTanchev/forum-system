package com.team3.forum;

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
    public void findAll_Should_Call_Repository(){
        //Arrange
        Mockito.when(mockUserRepository.findAll()).thenReturn(List.of());
        //Act
        userService.findAll();
        //Assert
        Mockito.verify(mockUserRepository,Mockito.times(1)).findAll();
    }

    @Test
    public void findById_Should_Call_Repository(){
        //Arrange
        var mockUser = createMockUser();
        Mockito.when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(mockUser);
        //Act
        User result =userService.findById(mockUser.getId());
        //Assert
        Assertions.assertEquals(result,mockUser);
    }


}
