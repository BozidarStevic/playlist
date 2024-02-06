package com.project.playlist.service.impl;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.User;
import com.project.playlist.repository.PlaylistRepository;
import com.project.playlist.service.PlaylistService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    private PlaylistService playlistService;
    @Mock
    private PlaylistRepository playlistRepositoryMock;
    @Mock
    private UserService userServiceMock;

    private User user;
    private Playlist actualPlaylist;
    private Playlist expectedEmptyPlaylist;
    private Playlist emptyPlaylistToSave;
    private final Long invalidUserId = 111L;
    private final Long invalidPlaylistId = 111L;


    @BeforeEach
    void setUp() {
        playlistService = new PlaylistServiceImpl(playlistRepositoryMock, userServiceMock);
        user = User.builder()
                .id(1L)
                .username("username")
                .email("email")
                .password("pass")
                .playlists(new ArrayList<>())
                .videos(new ArrayList<>())
                .build();
        emptyPlaylistToSave = Playlist.builder()
                .name("playlistName")
                .user(user)
                .playlistVideos(new ArrayList<>())
                .build();
        expectedEmptyPlaylist = Playlist.builder()
                .id(1L)
                .name("playlistName")
                .user(user)
                .playlistVideos(new ArrayList<>())
                .build();
        lenient().when(userServiceMock.getUserById(eq(1L))).thenReturn(user);
    }

    @Test
    void givenUserAndPlaylistName_whenCreateEmptyPlaylist_thenReturnCreatedPlaylist() {
        //given
        lenient().when(playlistRepositoryMock.save(emptyPlaylistToSave)).thenReturn(expectedEmptyPlaylist);
        //when
        actualPlaylist = playlistService.createEmptyPlaylist(1L, "playlistName");
        //then
        verifyAll(1, 1, 0, 0);
        assertEquals(expectedEmptyPlaylist, actualPlaylist);
    }

    @Test
    void givenNonexistentUserAndPlaylistName_whenCreateEmptyPlaylist_thenThrowException() {
        //given
        lenient().when(userServiceMock.getUserById(eq(invalidUserId))).thenThrow(UserNotFoundException.class);
        //when
        Executable executable = () -> playlistService.createEmptyPlaylist(invalidUserId, "playlistName");
        //then
        verifyAll(0, 0, 0, 0);
        assertThrows(UserNotFoundException.class, executable);
    }

    @Test
    void givenNullUserIdAndPlaylistName_whenCreateEmptyPlaylist_thenThrowException() {
        //given
        lenient().when(userServiceMock.getUserById(eq(null))).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> playlistService.createEmptyPlaylist(null, "playlistName");
        //then
        verifyAll(0, 0, 0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistId_whenGetPlaylistById_thenReturnPlaylist() {
        //given
        lenient().when(playlistRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(expectedEmptyPlaylist));
        //when
        actualPlaylist = playlistService.getPlaylistById(1L);
        //then
        verifyAll(0, 0, 0, 1);
        assertEquals(expectedEmptyPlaylist, actualPlaylist);
    }

    @Test
    void givenInvalidPlaylistId_whenGetPlaylistById_thenThrowException() {
        //given
        lenient().when(playlistRepositoryMock.findById(invalidPlaylistId)).thenThrow(PlaylistNotFoundException.class);
        //when
        Executable executable = () -> playlistService.getPlaylistById(invalidPlaylistId);
        //then
        verifyAll(0, 0, 0, 0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenNullPlaylistId_whenGetPlaylistById_thenThrowException() {
        //given
        lenient().when(playlistRepositoryMock.findById(null)).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> playlistService.getPlaylistById(null);
        //then
        verifyAll(0, 0, 0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    private void verifyAll(int userServiceMockNum,
                           int playlistRepositoryMockNum,
                           int userServiceMockInvalidSaveNum,
                           int playlistRepositoryMockFindByIdNum) {
        verify(userServiceMock, times(userServiceMockNum)).getUserById(anyLong());
        verify(playlistRepositoryMock, times(playlistRepositoryMockNum)).save(any(Playlist.class));
        verify(userServiceMock, times(userServiceMockInvalidSaveNum)).getUserById(eq(invalidUserId));
        verify(playlistRepositoryMock, times(playlistRepositoryMockFindByIdNum)).findById(anyLong());
    }

}
