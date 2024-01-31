package com.project.playlist.service.impl;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.VideoNotFoundException;
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
public class PlaylistVideoServiceTest {
    private PlaylistVideoService playlistVideoService;
    @Mock
    private PlaylistService playlistServiceMock;
    @Mock
    private VideoService videoServiceMock;
    @Mock
    private PlaylistVideoRepository playlistVideoRepositoryMock;

    private final Long invalidPlaylistId = 111L;
    private final Long invalidVideoId = 111L;
    List<Video> actualVideoList;
    private Playlist playlist;
    private Video video;
    private Video video1;
    private Video video2;
    private Video video3;
    private PlaylistVideo playlistVideo1;
    private PlaylistVideo playlistVideo2;
    private PlaylistVideo playlistVideo3;
    private PlaylistVideo playlistVideoForSave;
    private List<PlaylistVideo> playlistVideoList;

    @BeforeEach
    void setUp() {
        playlist = Playlist.builder()
                .id(1L)
                .name("playlist1")
                .build();
        video = Video.builder()
                .id(4L)
                .name("video4")
                .build();
        video1 = Video.builder()
                .id(1L)
                .name("video1")
                .build();
        video2 = Video.builder()
                .id(2L)
                .name("video2")
                .build();
        video3 = Video.builder()
                .id(3L)
                .name("video3")
                .build();
        playlistVideo1 = PlaylistVideo.builder()
                .id(1L)
                .video(video1)
                .playlist(playlist)
                .orderNo(1)
                .build();
        playlistVideo2 = PlaylistVideo.builder()
                .id(2L)
                .video(video2)
                .playlist(playlist)
                .orderNo(2)
                .build();
        playlistVideo3 = PlaylistVideo.builder()
                .id(3L)
                .video(video3)
                .playlist(playlist)
                .orderNo(3)
                .build();
        playlistVideoList = new ArrayList<>(List.of(playlistVideo1, playlistVideo2, playlistVideo3));

        playlistVideoService = new PlaylistVideoServiceImpl(videoServiceMock, playlistServiceMock, playlistVideoRepositoryMock);
    }

    @Test
    void givenPlaylistWithVideos_whenGetSortedVideosForPlaylist_thenReturnSortedVideosForPlaylistByOrderNumber() {
        //given

        lenient().when(playlistServiceMock.getPlaylistById(eq(1L))).thenReturn(playlist);
        lenient().when(playlistVideoRepositoryMock.findByPlaylistIdOrderByOrderNo(eq(1L))).thenReturn(playlistVideoList);

        //when
        actualVideoList = playlistVideoService.getSortedVideosForPlaylist(1L);

        //then
        verifyAll(1, 1, 0,0);
        assertAll(
                () -> assertEquals("video1", actualVideoList.get(0).getName()),
                () -> assertEquals("video2", actualVideoList.get(1).getName()),
                () -> assertEquals("video3", actualVideoList.get(2).getName())
        );
    }

    @Test
    void givenNonexistentPlaylist_whenGetSortedVideosForPlaylist_thenThrowException() {
        //given

        lenient().when(playlistServiceMock.getPlaylistById(eq(invalidPlaylistId))).thenThrow(PlaylistNotFoundException.class);

        //when
        Executable executable = () -> playlistVideoService.getSortedVideosForPlaylist(invalidPlaylistId);

        //then
        verifyAll(0, 0, 0,0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenNull_whenGetSortedVideosForPlaylist_thenThrowException() {
        //given

        lenient().when(playlistServiceMock.getPlaylistById(eq(null))).thenThrow(IllegalArgumentException.class);

        //when
        Executable executable = () -> playlistVideoService.getSortedVideosForPlaylist(null);

        //then
        verifyAll(0, 0, 0,0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistAndVideo_whenAddVideoToPlaylist_thenReturnPlaylistVideoListWithAddedVideoToTheEnd() {
        //given
        playlist.setPlaylistVideos(playlistVideoList);

        playlistVideoForSave = PlaylistVideo.builder()
                .playlist(playlist)
                .video(video)
                .orderNo(playlistVideoList.size() + 1)
                .build();

        lenient().when(playlistServiceMock.getPlaylistById(eq(1L))).thenReturn(playlist);
        lenient().when(videoServiceMock.getVideoById(eq(4L))).thenReturn(video);
        lenient().when(playlistVideoRepositoryMock.save(playlistVideoForSave)).thenReturn(playlistVideoForSave);

        //when
        PlaylistVideo playlistVideo = playlistVideoService.addVideoToPlaylist(1L, 4L);

        //then
        verifyAll(1, 0, 1,1);
        assertEquals(playlistVideoForSave, playlistVideo);
    }

    @Test
    void givenNonexistentPlaylistAndVideo_whenAddVideoToPlaylist_thenThrowException() {
        //given

        lenient().when(playlistServiceMock.getPlaylistById(eq(invalidPlaylistId))).thenThrow(PlaylistNotFoundException.class);

        //when
        Executable executable = () -> playlistVideoService.addVideoToPlaylist(invalidPlaylistId, 4L);

        //then
        verifyAll(0, 0, 0,0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenPlaylistAndNonexistentVideo_whenAddVideoToPlaylist_thenThrowException() {
        //given

        lenient().when(playlistServiceMock.getPlaylistById(eq(1L))).thenReturn(playlist);
        lenient().when(videoServiceMock.getVideoById(eq(invalidVideoId))).thenThrow(VideoNotFoundException.class);

        //when
        Executable executable = () -> playlistVideoService.addVideoToPlaylist(4L, invalidVideoId);

        //then
        verifyAll(0, 0, 0,0);
        assertThrows(VideoNotFoundException.class, executable);
    }

    private void verifyAll(int playlistServiceMockNum,
                           int playlistVideoRepositoryMockFindByPlaylistIdOrderByOrderNoNum,
                           int playlistVideoRepositoryMockSaveNum,
                           int videoServiceMockNum) {
        verify(playlistServiceMock, times(playlistServiceMockNum)).getPlaylistById(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockFindByPlaylistIdOrderByOrderNoNum)).findByPlaylistIdOrderByOrderNo(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockSaveNum)).save(any(PlaylistVideo.class));
        verify(videoServiceMock, times(videoServiceMockNum)).getVideoById(anyLong());
    }

}
