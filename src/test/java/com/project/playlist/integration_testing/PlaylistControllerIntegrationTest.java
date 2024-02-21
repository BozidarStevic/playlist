package com.project.playlist.integration_testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.integration_testing.util.MySqlIntegrationTest;
import com.project.playlist.model.*;
import com.project.playlist.repository.*;
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
public class PlaylistControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    PlaylistVideoRepository playlistVideoRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private User user, testUser;
    private Role testRole;
    private Video video1, video2;
    private Playlist playlist1, playlist2;
    private PlaylistVideo pv1, pv2;

    @BeforeEach
    void setUp() throws Exception {
        user = userRepository.save(User.builder().username("username").email("email").password("pass").build());
        video1 = videoRepository.save(Video.builder().url("url1").name("videoName1").description("videoDescription1").user(user).build());
        video2 = videoRepository.save(Video.builder().url("url2").name("videoName2").description("videoDescription2").user(user).build());
        playlist1 = playlistRepository.save(Playlist.builder().user(user).name("playlistName1").build());
        pv1 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video1).orderNo(1).build());
        pv2 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video2).orderNo(2).build());
        playlist1.setPlaylistVideos(new ArrayList<>(List.of(pv1, pv2)));
        playlist1 = playlistRepository.save(playlist1);
        playlist2 = playlistRepository.save(Playlist.builder().name("playlistName2").user(user).build());

        //Authentication
        testRole = roleRepository.save(Role.builder().name("ROLE_USER").build());
        Set<Role> roles = new HashSet<>();
        roles.add(testRole);
        testUser = userRepository.save(User.builder().username("username1").email("email1@gmail.com").password(passwordEncoder.encode("pass1")).roles(roles).build());
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistId_whenGetPlaylistById_thenReturnPlaylistWithVideos() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        //act
        MvcResult result = mvc.perform(get("/api/playlists/" + playlistId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PlaylistDTO playlistDto = objectMapper.readValue(result.getResponse().getContentAsString(), PlaylistDTO.class);
        //assert
        assertAll(
                () -> assertEquals("playlistName1", playlistDto.getName()),
                () -> assertEquals("username", playlistDto.getUser().getUsername()),
                () -> assertEquals(2, playlistDto.getVideos().size()),
                () -> assertEquals("videoName1", playlistDto.getVideos().getFirst().getName()),
                () -> assertEquals(1, playlistDto.getVideos().getFirst().getOrderNo()),
                () -> assertEquals("videoName2", playlistDto.getVideos().get(1).getName()),
                () -> assertEquals(2, playlistDto.getVideos().get(1).getOrderNo())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingPlaylistId_whenGetPlaylistById_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistIdNonExist = "99999";
        //act
        //assert
        mvc.perform(get("/api/playlists/" + playlistIdNonExist)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingUserIdAndPlaylistName_whenCreateEmptyPlaylist_thenReturnEmptyPlaylist() throws Exception {
        //arrange
        String userId = String.valueOf(user.getId());
        String playlistName = "newPlaylistName";
        //act
        MvcResult result = mvc.perform(post("/api/playlists/user/" + userId + "?playlistName=" + playlistName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PlaylistDTO playlistDto = objectMapper.readValue(result.getResponse().getContentAsString(), PlaylistDTO.class);
        Playlist playlist = playlistRepository.findById(playlistDto.getId()).get();
        List<Playlist> allPlaylists = playlistRepository.findAll();
        //assert
        assertAll(
                () -> assertEquals(3, allPlaylists.size()),
                () -> assertEquals("newPlaylistName", playlist.getName()),
                () -> assertEquals("username", playlist.getUser().getUsername()),
                () -> assertEquals(0, playlist.getPlaylistVideos().size()),
                () -> assertEquals("newPlaylistName", playlistDto.getName()),
                () -> assertEquals("username", playlistDto.getUser().getUsername()),
                () -> assertEquals(0, playlistDto.getVideos().size())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingUserIdAndPlaylistName_whenCreateEmptyPlaylist_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String userIdNonExist = "99999";
        String playlistName = "newPlaylistName";
        //act
        //assert
        mvc.perform(post("/api/playlists/user/" + userIdNonExist + "?playlistName=" + playlistName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenUserIdAndBlankPlaylistName_whenCreateEmptyPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String userIdNonExist = "1";
        String playlistName = "";
        //act
        //assert
        mvc.perform(post("/api/playlists/user/" + userIdNonExist + "?playlistName=" + playlistName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

}
