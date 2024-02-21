package com.project.playlist.service.impl;

import com.project.playlist.dto.UserRequest;
import com.project.playlist.exceptions.UserAlreadyExistsException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.model.Role;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.service.RoleService;
import com.project.playlist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

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
    @Mock
    private RoleService roleServiceMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    private UserRequest userRequest, existingUserRequest;
    private User user, actualUser, existingUser;
    private Role existingRole, roleToSave;
    private final Long invalidUserId = 111L;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(passwordEncoderMock, userRepositoryMock, roleServiceMock);
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
        roleToSave = Role.builder()
                .name("ROLE_USER")
                .build();
        existingRole = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();
        user = User.builder()
                .username("nonexistentUsername")
                .email("email")
                .password("pass")
                .playlists(new ArrayList<>())
                .videos(new ArrayList<>())
                .roles(Set.of(existingRole))
                .build();
        existingUser = User.builder()
                .id(1L)
                .username("existingUsername")
                .email("email")
                .password("pass")
                .playlists(new ArrayList<>())
                .videos(new ArrayList<>())
                .roles(Set.of(existingRole))
                .build();
    }

    @Test
    void givenUserRequest_whenRegisterUser_thenReturnRegisteredUser() {
        //given
        lenient().when(userRepositoryMock.findByUsername(eq("nonexistentUsername"))).thenReturn(Optional.empty());
        lenient().when(roleServiceMock.existsRoleByName(eq("ROLE_USER"))).thenReturn(true);
        lenient().when(roleServiceMock.getRoleByName(eq("ROLE_USER"))).thenReturn(existingRole);
        lenient().when(passwordEncoderMock.encode(eq(userRequest.getPassword()))).thenReturn("encodedPass");
        user.setPassword("encodedPass");
        lenient().when(userRepositoryMock.save(user)).thenReturn(user);
        //when
        actualUser = userService.registerUser(userRequest);
        //then
        verifyAll(1, 1, 0);
        assertEquals(user, actualUser);
    }

    @Test
    void givenExistingUserRequest_whenRegisterUser_thenThrowUserAlreadyExistsException() {
        //given
        lenient().when(userRepositoryMock.findByUsername(eq("existingUsername"))).thenReturn(Optional.ofNullable(existingUser));
        //when
        Executable executable = () -> userService.registerUser(existingUserRequest);
        //then
        verifyAll(0, 0, 0);
        assertThrows(UserAlreadyExistsException.class, executable);
    }

    @Test
    void givenNonexistentRoleName_whenRegisterUser_thenReturnRegisteredUser() {
        //given
        lenient().when(userRepositoryMock.findByUsername(eq("nonexistentUsername"))).thenReturn(Optional.empty());
        lenient().when(roleServiceMock.existsRoleByName(eq("ROLE_USER"))).thenReturn(false);
        lenient().when(roleServiceMock.createRole(eq(roleToSave))).thenReturn(existingRole);
        lenient().when(roleServiceMock.getRoleByName(eq("ROLE_USER"))).thenReturn(existingRole);
        lenient().when(passwordEncoderMock.encode(eq(userRequest.getPassword()))).thenReturn("encodedPass");
        user.setPassword("encodedPass");
        lenient().when(userRepositoryMock.save(user)).thenReturn(user);
        //when
        actualUser = userService.registerUser(userRequest);
        //then
        verifyAll(1, 1, 0);
        assertEquals(user, actualUser);
    }

    @Test
    void givenNullUserRequest_whenRegisterUser_thenThrowIllegalArgumentException() {
        //given
        //when
        Executable executable = () -> userService.registerUser(null);
        //then
        verifyAll(0, 0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenUserId_whenGetUserById_thenReturnUser() {
        //given
        lenient().when(userRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(existingUser));
        //when
        actualUser = userService.getUserById(1L);
        //then
        verifyAll(0, 0, 1);
        assertEquals(existingUser, actualUser);
    }

    @Test
    void givenInvalidUserId_whenGetUserById_thenThrowUserNotFoundException() {
        //given
        lenient().when(userRepositoryMock.findById(invalidUserId)).thenReturn(Optional.empty());
        //when
        Executable executable = () -> userService.getUserById(invalidUserId);
        //then
        verifyAll(0, 0, 0);
        assertThrows(UserNotFoundException.class, executable);
    }

    @Test
    void givenNullUserId_whenGetUserById_thenThrowIllegalArgumentException() {
        //given
        lenient().when(userRepositoryMock.findById(null)).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> userService.getUserById(null);
        //then
        verifyAll(0 ,0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    private void verifyAll(int userRepositoryMockFindByUsernameNum,
                           int userRepositoryMockSaveNum,
                           int userRepositoryMockFindByIdNum
    ) {
        verify(userRepositoryMock, times(userRepositoryMockFindByUsernameNum)).findByUsername(anyString());
        verify(userRepositoryMock, times(userRepositoryMockSaveNum)).save(any(User.class));
        verify(userRepositoryMock, times(userRepositoryMockFindByIdNum)).findById(anyLong());
    }

}
