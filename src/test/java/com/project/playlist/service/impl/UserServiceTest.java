package com.project.playlist.service.impl;

import com.project.playlist.dto.UserRequest;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.exceptions.UserAlreadyExistsException;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepositoryMock;
    private UserRequest userRequest;
    private User user;
    private User actualUser;
    private UserRequest existingUserRequest;
    private User existingUser;
    private final Long invalidUserId = 111L;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepositoryMock);
        userRequest = UserRequest.builder()
                .email("email")
                .password("pass")
                .username("nonexistentUsername")
                .build();
        existingUserRequest = UserRequest.builder()
                .email("email")
                .password("pass")
                .username("existingUsername")
                .build();
        user = User.builder()
                .username("nonexistentUsername")
                .email("email")
                .password("pass")
                .playlists(new ArrayList<>())
                .videos(new ArrayList<>())
                .build();
        existingUser = User.builder()
                .id(1L)
                .username("existingUsername")
                .email("email")
                .password("pass")
                .playlists(new ArrayList<>())
                .videos(new ArrayList<>())
                .build();
    }

    @Test
    void givenUserRequest_whenRegisterUser_thenReturnRegisteredUser() {
        //given
        lenient().when(userRepositoryMock.findByUsername(eq("nonexistentUsername"))).thenReturn(Optional.empty());
        lenient().when(userRepositoryMock.save(user)).thenReturn(user);
        //when
        actualUser = userService.registerUser(userRequest);
        //then
        assertEquals(user, actualUser);
        verify(userRepositoryMock, times(1)).findByUsername(anyString());
        verify(userRepositoryMock, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingUserRequest_whenRegisterUser_thenThrowException() {
        //given
        lenient().when(userRepositoryMock.findByUsername(eq("existingUsername"))).thenReturn(Optional.ofNullable(existingUser));
        //when
        Executable executable = () -> userService.registerUser(existingUserRequest);
        //then
        assertThrows(UserAlreadyExistsException.class, executable);
        verify(userRepositoryMock, times(1)).findByUsername(anyString());
        verify(userRepositoryMock, times(0)).save(any(User.class));
    }

    @Test
    void givenNullUserRequest_whenRegisterUser_thenThrowException() {
        //given
        //when
        Executable executable = () -> userService.registerUser(null);
        //then
        assertThrows(IllegalArgumentException.class, executable);
        verify(userRepositoryMock, times(0)).findByUsername(anyString());
        verify(userRepositoryMock, times(0)).save(any(User.class));
    }

    @Test
    void givenUserId_whenGetUserById_thenReturnUser() {
        //given
        lenient().when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(existingUser));
        //when
        actualUser = userService.getUserById(1L);
        //then
        assertEquals(existingUser, actualUser);
        verify(userRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    void givenInvalidUserId_whenGetUserById_thenThrowException() {
        //given
        lenient().when(userRepositoryMock.findById(invalidUserId)).thenReturn(Optional.empty());
        //when
        Executable executable = () -> userService.getUserById(invalidUserId);
        //then
        assertThrows(UserNotFoundException.class, executable);
        verify(userRepositoryMock, times(1)).findById(anyLong());
    }

    @Test
    void givenNullUserId_whenGetUserById_thenThrowException() {
        //given
        lenient().when(userRepositoryMock.findById(null)).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> userService.getUserById(null);
        //then
        assertThrows(IllegalArgumentException.class, executable);
        verify(userRepositoryMock, times(0)).findById(anyLong());
    }

}
