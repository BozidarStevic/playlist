package com.project.playlist.service.impl;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.VideoAlreadyInPlaylistException;
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

import java.util.*;

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
    private List<Video> actualVideoList;
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
        playlist.setPlaylistVideos(playlistVideoList);
        playlistVideoService = new PlaylistVideoServiceImpl(videoServiceMock, playlistServiceMock, playlistVideoRepositoryMock);
        lenient().when(playlistServiceMock.getPlaylistById(eq(1L))).thenReturn(playlist);
        lenient().when(playlistServiceMock.getPlaylistById(eq(invalidPlaylistId))).thenThrow(PlaylistNotFoundException.class);
        lenient().when(videoServiceMock.getVideoById(eq(2L))).thenReturn(video2);
        lenient().when(videoServiceMock.getVideoById(eq(invalidVideoId))).thenThrow(VideoNotFoundException.class);
        lenient().when(playlistVideoRepositoryMock.findByPlaylistId(1L)).thenReturn(playlistVideoList);
    }

    @Test
    void givenPlaylistWithVideos_whenGetSortedVideosForPlaylist_thenReturnSortedVideosForPlaylistByOrderNumber() {
        //given
        lenient().when(playlistVideoRepositoryMock.findByPlaylistIdOrderByOrderNo(eq(1L))).thenReturn(playlistVideoList);
        //when
        actualVideoList = playlistVideoService.getSortedVideosForPlaylist(1L);
        //then
        verifyAll(1, 1, 0,0,0, 0, 0);
        assertAll(
                () -> assertEquals("video1", actualVideoList.get(0).getName()),
                () -> assertEquals("video2", actualVideoList.get(1).getName()),
                () -> assertEquals("video3", actualVideoList.get(2).getName())
        );
    }

    @Test
    void givenNonexistentPlaylist_whenGetSortedVideosForPlaylist_thenThrowPlaylistNotFoundException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.getSortedVideosForPlaylist(invalidPlaylistId);
        //then
        verifyAll(0, 0, 0,0,0, 0, 0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenNull_whenGetSortedVideosForPlaylist_thenThrowIllegalArgumentException() {
        //given
        lenient().when(playlistServiceMock.getPlaylistById(eq(null))).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> playlistVideoService.getSortedVideosForPlaylist(null);
        //then
        verifyAll(0, 0, 0,0,0, 0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistAndVideo_whenAddVideoToPlaylist_thenReturnPlaylistVideoListWithAddedVideoToTheEnd() {
        //given
        playlistVideoForSave = PlaylistVideo.builder()
                .playlist(playlist)
                .video(video)
                .orderNo(playlistVideoList.size() + 1)
                .build();
        lenient().when(videoServiceMock.getVideoById(eq(4L))).thenReturn(video);
        Map<Long, PlaylistVideo> database = new HashMap<>();
        lenient().when(playlistVideoRepositoryMock.save(playlistVideoForSave)).thenAnswer(answer -> {
            playlistVideoForSave.setId(5L);
            database.put(playlistVideoForSave.getId(), playlistVideoForSave);
            return playlistVideoForSave;
        });
        PlaylistVideo playlistVideo = playlistVideoService.addVideoToPlaylist(1L, 4L);
        //then
        verifyAll(1, 0, 1,1,0,0, 0);
        assertEquals(playlistVideoForSave, playlistVideo);
        assertEquals(playlistVideoForSave, database.get(5L));
    }

    @Test
    void givenNonexistentPlaylistAndVideo_whenAddVideoToPlaylist_thenThrowPlaylistNotFoundException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.addVideoToPlaylist(invalidPlaylistId, 4L);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenPlaylistAndNonexistentVideo_whenAddVideoToPlaylist_thenThrowVideoNotFoundException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.addVideoToPlaylist(4L, invalidVideoId);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(VideoNotFoundException.class, executable);
    }

    @Test
    void givenPlaylistAndVideoWhichIsAlreadyInPlaylist_whenAddVideoToPlaylist_thenThrowVideoAlreadyInPlaylistException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.addVideoToPlaylist(1L, 2L);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(VideoAlreadyInPlaylistException.class, executable);
    }

    @Test
    void givenPlaylistAndVideo_whenRemoveVideoFromPlaylist_thenReturnNothing() {
        //given
        lenient().when(playlistVideoRepositoryMock.findByPlaylistIdAndVideoId(1L, 2L)).thenReturn(Optional.ofNullable(playlistVideo2));
        lenient().when(playlistVideoRepositoryMock.saveAll(anyList())).thenReturn(new ArrayList<>());
        //when
        playlistVideoService.removeVideoFromPlaylist(1L, 2L);
        //then
        verifyAll(1, 0, 0,1,1,1, 1);
    }

    @Test
    void givenNonexistentPlaylistAndVideo_whenRemoveVideoFromPlaylist_thenThrowPlaylistNotFoundException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.removeVideoFromPlaylist(invalidPlaylistId, 4L);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenPlaylistAndNonexistentVideo_whenRemoveVideoFromPlaylist_thenThrowVideoNotFoundException() {
        //given
        lenient().when(playlistServiceMock.getPlaylistById(eq(1L))).thenReturn(playlist);
        //when
        Executable executable = () -> playlistVideoService.removeVideoFromPlaylist(1L, invalidVideoId);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(VideoNotFoundException.class, executable);
    }

    @Test
    void givenPlaylistAndVideoWhichIsNotInPlaylist_whenRemoveVideoFromPlaylist_thenThrowIllegalArgumentException() {
        //given
        lenient().when(playlistVideoRepositoryMock.findByPlaylistIdAndVideoId(1L, 4L)).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> playlistVideoService.removeVideoFromPlaylist(1L, 4L);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistAndFromOrderNoGreaterThanToOrderNo_whenChangeVideoOrder_thenReturnNothing() {
        //given
        //when
        playlistVideoService.changeVideoOrder(1L, 3,2);
        //then
        verifyAll(1, 0, 1,0,0, 1,1);
    }

    @Test
    void givenPlaylistAndFromOrderNoLessThanToOrderNo_whenChangeVideoOrder_thenReturnNothing() {
        //given
        //when
        playlistVideoService.changeVideoOrder(1L, 2,3);
        //then
        verifyAll(1, 0, 1,0,0, 1,1);
    }

    @Test
    void givenNonexistentPlaylistAndOrderNumbers_whenChangeVideoOrder_thenThrowPlaylistNotFoundException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.changeVideoOrder(invalidPlaylistId, 3,2);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(PlaylistNotFoundException.class, executable);
    }

    @Test
    void givenEmptyPlaylistAndOrderNumbers_whenChangeVideoOrder_thenThrowIllegalArgumentException() {
        //given
        playlistVideoList = new ArrayList<>();
        lenient().when(playlistVideoRepositoryMock.findByPlaylistId(1L)).thenReturn(playlistVideoList);
        //when
        Executable executable = () -> playlistVideoService.changeVideoOrder(1L, 3,2);
        //then
        verifyAll(0, 0, 0,0,0,0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistAndEqualOrderNumbers_whenChangeVideoOrder_thenThrowIllegalArgumentException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.changeVideoOrder(1L, 2,2);
        //then
        verifyAll(0, 0, 0,0,0,0,0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistAndNegativeOrderNumbers_whenChangeVideoOrder_thenThrowIllegalArgumentException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.changeVideoOrder(1L, -2, -3);
        //then
        verifyAll(0, 0, 0,0,0,0,0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenPlaylistAndOrderNumbersGreaterThanPlaylistSize_whenChangeVideoOrder_thenIllegalArgumentException() {
        //given
        //when
        Executable executable = () -> playlistVideoService.changeVideoOrder(1L, playlistVideoList.size() + 1, playlistVideoList.size() + 2);
        //then
        verifyAll(0, 0, 0,0,0,0,0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    private void verifyAll(int playlistServiceMockGetPlaylistByIdNum,
                           int playlistVideoRepositoryMockFindByPlaylistIdOrderByOrderNoNum,
                           int playlistVideoRepositoryMockSaveNum,
                           int videoServiceMockGetVideoByIdNum,
                           int playlistVideoRepositoryMockDeleteNum,
                           int playlistVideoRepositoryMockFindByPlaylistIdNum,
                           int playlistVideoRepositoryMockSaveAllNum) {
        verify(playlistServiceMock, times(playlistServiceMockGetPlaylistByIdNum)).getPlaylistById(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockFindByPlaylistIdOrderByOrderNoNum)).findByPlaylistIdOrderByOrderNo(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockSaveNum)).save(any(PlaylistVideo.class));
        verify(videoServiceMock, times(videoServiceMockGetVideoByIdNum)).getVideoById(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockDeleteNum)).delete(any(PlaylistVideo.class));
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockFindByPlaylistIdNum)).findByPlaylistId(anyLong());
        verify(playlistVideoRepositoryMock, times(playlistVideoRepositoryMockSaveAllNum)).saveAll(anyList());
    }

}
