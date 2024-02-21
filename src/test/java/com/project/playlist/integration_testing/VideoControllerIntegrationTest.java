package com.project.playlist.integration_testing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.VideoDTO;
import com.project.playlist.dto.VideoRequest;
import com.project.playlist.integration_testing.util.MySqlIntegrationTest;
import com.project.playlist.model.Role;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import com.project.playlist.repository.RoleRepository;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.repository.VideoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class VideoControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private User user, testUser;
    private Role testRole;
    private Video video1, video2;
    private VideoRequest videoRequest;

    @BeforeEach
    void setUp() throws Exception {
        user = userRepository.save(User.builder().username("username").email("email@gmail.com").password("pass1").build());
        video1 = videoRepository.save(Video.builder().url("http://www.example.com/video1").name("videoName1").description("videoDescription1").user(user).build());
        video2 = videoRepository.save(Video.builder().url("http://www.example.com/video2").name("videoName2").description("videoDescription2").user(user).build());

        //Authentication
        testRole = roleRepository.save(Role.builder().name("ROLE_USER").build());
        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        testUser = userRepository.save(User.builder().username("username1").email("email1@gmail.com").password(passwordEncoder.encode("pass1")).roles(roles).build());
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
        VideoDTO videoDTO = objectMapper.readValue(result.getResponse().getContentAsString(), VideoDTO.class);
        List<Video> allVideos = videoRepository.findAll();
        //assert
        assertAll(
                () -> assertEquals(3, allVideos.size()),
                () -> assertEquals("videoName3", videoDTO.getName()),
                () -> assertEquals("http://www.example.com/video3", videoDTO.getUrl()),
                () -> assertEquals("videoDescription3", videoDTO.getDescription()),
                () -> assertEquals(user.getId(), videoDTO.getUser().getId())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void given_whenGetAllVideos_thenReturnAllVideos() throws Exception {
        //arrange
        //act
        MvcResult result = mvc.perform(get("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ArrayList<VideoDTO> videoDtoList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        //assert
        assertAll(
                () -> assertEquals(2, videoDtoList.size()),
                () -> assertEquals("videoName1", videoDtoList.getFirst().getName()),
                () -> assertEquals("videoName2", videoDtoList.get(1).getName())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingVideoId_whenGetVideoById_thenReturnExpectedVideo() throws Exception {
        //arrange
        String videoId = String.valueOf(video1.getId());
        //act
        MvcResult result = mvc.perform(get("/api/videos/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        VideoDTO videoDto = objectMapper.readValue(result.getResponse().getContentAsString(), VideoDTO.class);
        //assert
        assertAll(
                () -> assertEquals("videoName1", videoDto.getName()),
                () -> assertEquals("http://www.example.com/video1", videoDto.getUrl()),
                () -> assertEquals("videoDescription1", videoDto.getDescription()),
                () -> assertEquals("username", videoDto.getUser().getUsername())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
