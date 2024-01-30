package com.project.playlist.service.impl;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.Video;
import com.project.playlist.repository.PlaylistVideoRepository;
import com.project.playlist.service.PlaylistService;
import com.project.playlist.service.PlaylistVideoService;
import com.project.playlist.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class GetSortedVideosForPlaylistTest {
    private PlaylistVideoService playlistVideoService;
    @Mock
    private PlaylistService playlistServiceMock;
    @Mock
    private VideoService videoServiceMock;
    @Mock
    private PlaylistVideoRepository playlistVideoRepositoryMock;

    private Long invalidPlaylistId;

    @BeforeEach
    void setUp() {
        playlistVideoService = new PlaylistVideoServiceImpl(videoServiceMock, playlistServiceMock, playlistVideoRepositoryMock);
    }

    @Test
    void givenPlaylistWithVideos_whenSortVideosInPlaylist_thenReturnSortedVideosForPlaylistByOrderNumber() {
        //given
        final Long correctPlaylistId = 1L;
        List<Video> actualVideoList;
        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setName("playlistName");
        Video video1 = new Video();
        video1.setId(1L);
        video1.setName("video1");
        Video video2 = new Video();
        video2.setId(1L);
        video2.setName("video2");
        Video video3 = new Video();
        video3.setId(3L);
        video3.setName("video3");
        PlaylistVideo playlistVideo1 = new PlaylistVideo(1L, video1, playlist, 1);
        PlaylistVideo playlistVideo2 = new PlaylistVideo(2L, video2, playlist, 2);
        PlaylistVideo playlistVideo3 = new PlaylistVideo(3L, video3, playlist, 3);
        List<PlaylistVideo> playlistVideoList = new ArrayList<>(List.of(playlistVideo1, playlistVideo2, playlistVideo3));

        when(playlistServiceMock.getPlaylistById(eq(correctPlaylistId))).thenReturn(playlist);
        when(playlistVideoRepositoryMock.findByPlaylistIdOrderByOrderNo(correctPlaylistId)).thenReturn(playlistVideoList);

        //when
        actualVideoList = playlistVideoService.getSortedVideosForPlaylist(1L);

        //then
        verifyAll(1, 1);
        assertAll(
                () -> assertEquals("video1", actualVideoList.get(0).getName()),
                () -> assertEquals("video2", actualVideoList.get(1).getName()),
                () -> assertEquals("video3", actualVideoList.get(2).getName())
        );
    }

    @Test
    void givenNonexistentPlaylist_whenSortVideosInNonexistentPlaylist_thenThrowException() {
        //given
        invalidPlaylistId = 111L;
        when(playlistServiceMock.getPlaylistById(eq(invalidPlaylistId))).thenThrow(PlaylistNotFoundException.class);

        //when
        Executable executable = () -> playlistVideoService.getSortedVideosForPlaylist(invalidPlaylistId);

        //then
        verifyAll(0, 0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenNull_whenSortVideosInNonexistentPlaylist_thenThrowException() {
        //given
        invalidPlaylistId = null;
        when(playlistServiceMock.getPlaylistById(eq(invalidPlaylistId))).thenThrow(IllegalArgumentException.class);

        //when
        Executable executable = () -> playlistVideoService.getSortedVideosForPlaylist(invalidPlaylistId);

        //then
        verifyAll(0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    private void verifyAll(int playlistServiceMockNum,
                           int playlistVideoRepositoryMockNum) {
        verify(playlistServiceMock, times(playlistServiceMockNum)).getPlaylistById(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockNum)).findByPlaylistIdOrderByOrderNo(anyLong());
    }

}
