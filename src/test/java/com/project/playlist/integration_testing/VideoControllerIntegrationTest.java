package com.project.playlist.integration_testing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.integration_testing.util.MySqlIntegrationTest;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.repository.VideoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VideoControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private User user;
    private Video video1, video2;
    private VideoRequest videoRequest;

    @BeforeEach
    void setUp() throws Exception {
        user = userRepository.save(User.builder().username("username").email("email@gmail.com").password("pass1").build());
        video1 = videoRepository.save(Video.builder().url("http://www.example.com/video1").name("videoName1").description("videoDescription1").user(user).build());
        video2 = videoRepository.save(Video.builder().url("http://www.example.com/video2").name("videoName2").description("videoDescription2").user(user).build());
    }

    @Test
    public void givenValidVideoRequest_whenCreateVideo_thenReturnCreatedVideo() throws Exception {
        //arrange
        videoRequest = VideoRequest.builder()
                .name("videoName3")
                .url("http://www.example.com/video3")
                .description("videoDescription3")
                .userId(user.getId())
                .build();
        String videoRequestJson = objectMapper.writeValueAsString(videoRequest);
        //act
        MvcResult result = mvc.perform(post("/api/videos")
                        .content(videoRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = result.getResponse().getContentAsString();
        VideoDTO videoDTO = objectMapper.readValue(jsonContent, VideoDTO.class);
        Video video = videoRepository.findById(videoDTO.getId()).get();
        List<Video> allVideos = videoRepository.findAll();
        //assert
        assertAll(
                () -> assertEquals(3, allVideos.size()),
                () -> assertEquals("videoName3", video.getName()),
                () -> assertEquals("http://www.example.com/video3", video.getUrl()),
                () -> assertEquals("videoDescription3", video.getDescription()),
                () -> assertEquals(user.getId(), video.getUser().getId())
        );
    }

    @Test
    public void givenExistingVideoUrlForUser_whenCreateVideo_thenExpectConflictStatus() throws Exception {
        //arrange
        videoRequest = VideoRequest.builder()
                .name("videoName3")
                .url("http://www.example.com/video1")
                .description("videoDescription3")
                .userId(user.getId())
                .build();
        String videoRequestJson = objectMapper.writeValueAsString(videoRequest);
        //act
        //assert
        mvc.perform(post("/api/videos")
                        .content(videoRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }

    @Test
    public void givenNullVideoRequest_whenCreateVideo_thenExpectBadRequestStatus() throws Exception {
        //arrange
        videoRequest = null;
        String videoRequestJson = objectMapper.writeValueAsString(videoRequest);
        //act
        //assert
        mvc.perform(post("/api/videos")
                        .content(videoRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void givenBlankVideoName_whenCreateVideo_thenExpectBadRequestStatus() throws Exception {
        //arrange
        videoRequest = VideoRequest.builder()
                .name("  ")
                .url("http://www.example.com/video3")
                .description("videoDescription3")
                .userId(user.getId())
                .build();
        String videoRequestJson = objectMapper.writeValueAsString(videoRequest);
        //act
        //assert
        mvc.perform(post("/api/videos")
                        .content(videoRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void givenBadUrl_whenCreateVideo_thenExpectBadRequestStatus() throws Exception {
        //arrange
        videoRequest = VideoRequest.builder()
                .name("videoName3")
                .url("badUrl")
                .description("videoDescription3")
                .userId(user.getId())
                .build();
        String videoRequestJson = objectMapper.writeValueAsString(videoRequest);
        //act
        //assert
        mvc.perform(post("/api/videos")
                        .content(videoRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void givenNullUserId_whenCreateVideo_thenExpectBadRequestStatus() throws Exception {
        //arrange
        videoRequest = VideoRequest.builder()
                .name("videoName3")
                .url("http://www.example.com/video3")
                .description("videoDescription3")
                .userId(null)
                .build();
        String videoRequestJson = objectMapper.writeValueAsString(videoRequest);
        //act
        //assert
        mvc.perform(post("/api/videos")
                        .content(videoRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void given_whenGetAllVideos_thenReturnAllVideos() throws Exception {
        //arrange
        //act
        MvcResult result = mvc.perform(get("/api/videos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = result.getResponse().getContentAsString();
        ArrayList<VideoDTO> videoDtoList = objectMapper.readValue(jsonContent, new TypeReference<ArrayList<VideoDTO>>() {});
        //assert
        assertAll(
                () -> assertEquals(2, videoDtoList.size()),
                () -> assertEquals("videoName1", videoDtoList.getFirst().getName()),
                () -> assertEquals("videoName2", videoDtoList.get(1).getName())
        );
    }

    @Test
    public void givenExistingVideoId_whenGetVideoById_thenReturnExpectedVideo() throws Exception {
        //arrange
        String videoId = String.valueOf(video1.getId());
        //act
        MvcResult result = mvc.perform(get("/api/videos/" + videoId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = result.getResponse().getContentAsString();
        VideoDTO videoDto = objectMapper.readValue(jsonContent, VideoDTO.class);
        //assert
        assertAll(
                () -> assertEquals("videoName1", videoDto.getName()),
                () -> assertEquals("http://www.example.com/video1", videoDto.getUrl()),
                () -> assertEquals("videoDescription1", videoDto.getDescription()),
                () -> assertEquals("username", videoDto.getUser().getUsername())
        );
    }

    @Test
    public void givenNonExistingVideoId_whenGetVideoById_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String videoIdNonExist = "99999";
        //act
        //assert
        mvc.perform(get("/api/videos/" + videoIdNonExist)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void givenNullVideoId_whenGetVideoById_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String videoIdNonExist = null;
        //act
        //assert
        mvc.perform(get("/api/videos/" + videoIdNonExist)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

}
