package com.project.playlist.service.impl;

import com.project.playlist.dto.VideoRequest;
import com.project.playlist.exceptions.DuplicateVideoUrlForUserException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.exceptions.VideoNotFoundException;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import com.project.playlist.repository.VideoRepository;
import com.project.playlist.service.UserService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {
    private VideoService videoService;
    @Mock
    private VideoRepository videoRepositoryMock;
    @Mock
    private UserService userServiceMock;
    private List<Video> videos;
    private Video video;
    private Video video1;
    private Video video2;
    private Video video3;
    private User user;
    private Video actualVideo;
    private VideoRequest videoRequest;
    private final Long invalidUserId = 111L;
    private final Long invalidVideoId = 111L;
    private Optional<Video> actualVideoOptional;

    @BeforeEach
    void setUp() {
        videoService = new VideoServiceImpl(videoRepositoryMock, userServiceMock);
        user = User.builder()
                .id(1L)
                .username("username")
                .email("email")
                .password("pass")
                .playlists(new ArrayList<>())
                .videos(new ArrayList<>())
                .build();
        video = Video.builder()
                .name("video1")
                .url("url")
                .description("description")
                .user(user)
                .playlistVideos(new ArrayList<>())
                .build();
        video1 = Video.builder()
                .id(1L)
                .name("video1")
                .user(user)
                .build();
        video2 = Video.builder()
                .id(2L)
                .name("video2")
                .user(user)
                .build();
        video3 = Video.builder()
                .id(3L)
                .name("video3")
                .user(user)
                .build();
        videos = new ArrayList<>(List.of(video1, video2, video3));
        videoRequest = VideoRequest.builder()
                .url("url")
                .description("description")
                .userId(1L)
                .name("video1")
                .build();

    }
    @Test
    void given_whenGetAllVideos_thenReturnAllVideos() {
        //given
        lenient().when(videoRepositoryMock.findAll()).thenReturn(videos);
        //when
        List<Video> actualVideos = videoService.getAllVideos();
        //then
        verifyAll(1, 0, 0, 0, 0);
        assertEquals(actualVideos, videos);
    }

    @Test
    void givenVideoRequest_whenCreateVideo_thenCreatedVideo() {
        //given
        lenient().when(userServiceMock.getUserById(eq(1L))).thenReturn(user);
        lenient().when(videoRepositoryMock.findByUrlAndUserId("url" ,1L)).thenReturn(Optional.empty());
        lenient().when(videoRepositoryMock.save(video)).thenReturn(video1);
        //when
        actualVideo = videoService.createVideo(videoRequest);
        //then
        verifyAll(0, 1, 1, 1, 0);
        assertEquals(video1, actualVideo);
    }

    @Test
    void givenNonexistentUser_whenCreateVideo_thenThrowUserNotFoundException() {
        videoRequest.setUserId(111L);
        //given
        lenient().when(userServiceMock.getUserById(eq(invalidUserId))).thenThrow(UserNotFoundException.class);
        //when
        Executable executable = () -> videoService.createVideo(videoRequest);
        //then
        verifyAll(0, 0, 0, 0, 0);
        assertThrows(UserNotFoundException.class, executable);
    }

    @Test
    void givenExistingVideoUrlForUser_whenCreateVideo_thenThrowDuplicateVideoUrlForUserException() {
        //given
        video1.setUrl("existingUrl");
        videoRequest.setUrl("existingUrl");
        lenient().when(userServiceMock.getUserById(eq(1L))).thenReturn(user);
        lenient().when(videoRepositoryMock.findByUrlAndUserId("existingUrl" ,1L)).thenReturn(Optional.ofNullable(video1));
        //when
        Executable executable = () -> videoService.createVideo(videoRequest);
        //then
        verifyAll(0, 0, 0, 0, 0);
        assertThrows(DuplicateVideoUrlForUserException.class, executable);
    }

    @Test
    void givenNullVideoRequest_whenCreateVideo_thenThrowIllegalArgumentException() {
        //given
        //when
        Executable executable = () -> videoService.createVideo(null);
        //then
        verifyAll(0, 0, 0, 0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void givenUrlAndUser_whenGetVideoByUrlAndUserId_thenReturnVideo() {
        //given
        video1.setUrl("existingUrl");
        lenient().when(videoRepositoryMock.findByUrlAndUserId("existingUrl" ,1L)).thenReturn(Optional.ofNullable(video1));
        //when
        actualVideoOptional = videoService.getVideoByUrlAndUserId("existingUrl", user);
        //then
        verifyAll(0, 0, 1, 0, 0);
        assertEquals(Optional.ofNullable(video1), actualVideoOptional);
    }

    @Test
    void givenNonexistentUrlAndUser_whenGetVideoByUrlAndUserId_thenReturnEmptyOptional() {
        //given
        video1.setUrl("nonexistentUrl");
        lenient().when(videoRepositoryMock.findByUrlAndUserId("nonexistentUrl" ,1L)).thenReturn(Optional.empty());
        //when
        actualVideoOptional = videoService.getVideoByUrlAndUserId("nonexistentUrl", user);
        //then
        verifyAll(0, 0, 1, 0, 0);
        assertEquals(Optional.empty(), actualVideoOptional);
    }

    @Test
    void givenUrlAndNonexistentUser_whenGetVideoByUrlAndUserId_thenReturnEmptyOptional() {
        //given
        user.setId(invalidUserId);
        lenient().when(videoRepositoryMock.findByUrlAndUserId("url" ,invalidUserId)).thenReturn(Optional.empty());
        //when
        actualVideoOptional = videoService.getVideoByUrlAndUserId("url", user);
        //then
        verifyAll(0, 0, 1, 0, 0);
        assertEquals(Optional.empty(), actualVideoOptional);
    }

    @Test
    void givenVideoId_whenGetVideoById_thenReturnVideo() {
        //given
        lenient().when(videoRepositoryMock.findById(1L)).thenReturn(Optional.ofNullable(video1));
        //when
        actualVideo = videoService.getVideoById(1L);
        //then
        verifyAll(0, 0, 0, 0, 1);
        assertEquals(video1, actualVideo);
    }

    @Test
    void givenInvalidVideoId_whenGetVideoById_thenThrowVideoNotFoundException() {
        //given
        lenient().when(videoRepositoryMock.findById(invalidVideoId)).thenReturn(Optional.empty());
        //when
        Executable executable = () -> videoService.getVideoById(invalidVideoId);
        //then
        verifyAll(0, 0, 0, 0, 0);
        assertThrows(VideoNotFoundException.class, executable);
    }

    @Test
    void givenNullVideoId_whenGetVideoById_thenThrowIllegalArgumentException() {
        //given
        lenient().when(videoRepositoryMock.findById(null)).thenThrow(IllegalArgumentException.class);
        //when
        Executable executable = () -> videoService.getVideoById(null);
        //then
        verifyAll(0, 0, 0, 0, 0);
        assertThrows(IllegalArgumentException.class, executable);
    }

    private void verifyAll(int videoRepositoryMockFindAllNum,
                           int userServiceMockGetUserByIdNum,
                           int videoRepositoryMockFindByUrlAndUserIdNum,
                           int videoRepositoryMockSaveNum,
                           int videoRepositoryMockFindByIdNum
                           ) {
        verify(videoRepositoryMock, times(videoRepositoryMockFindAllNum)).findAll();
        verify(userServiceMock, times(userServiceMockGetUserByIdNum)).getUserById(anyLong());
        verify(videoRepositoryMock, times(videoRepositoryMockFindByUrlAndUserIdNum)).findByUrlAndUserId(anyString(),anyLong());
        verify(videoRepositoryMock, times(videoRepositoryMockSaveNum)).save(any(Video.class));
        verify(videoRepositoryMock, times(videoRepositoryMockFindByIdNum)).findById(anyLong());
    }

}
