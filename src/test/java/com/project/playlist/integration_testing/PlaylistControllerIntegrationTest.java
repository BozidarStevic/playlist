package com.project.playlist.integration_testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.integration_testing.util.MySqlIntegrationTest;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.PlaylistVideo;
import com.project.playlist.model.User;
import com.project.playlist.model.Video;
import com.project.playlist.repository.PlaylistRepository;
import com.project.playlist.repository.PlaylistVideoRepository;
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
public class PlaylistControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    PlaylistVideoRepository playlistVideoRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private User user;
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
    }

    @Test
    public void givenExistingPlaylistId_whenGetPlaylistById_thenReturnPlaylistWithVideos() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        //act
        MvcResult result = mvc.perform(get("/api/playlists/" + playlistId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = result.getResponse().getContentAsString();
        PlaylistDTO playlistDto = objectMapper.readValue(jsonContent, PlaylistDTO.class);
        Playlist playlist = playlistRepository.findById(playlistDto.getId()).get();
        //assert
        assertAll(
                () -> assertEquals("playlistName1", playlist.getName()),
                () -> assertEquals("username", playlist.getUser().getUsername()),
                () -> assertEquals("videoName1", playlist.getPlaylistVideos().getFirst().getVideo().getName()),
                () -> assertEquals(1, playlist.getPlaylistVideos().getFirst().getOrderNo()),
                () -> assertEquals("videoName2", playlist.getPlaylistVideos().get(1).getVideo().getName()),
                () -> assertEquals(2, playlist.getPlaylistVideos().get(1).getOrderNo())
        );
    }

    @Test
    public void givenNonExistingPlaylistId_whenGetPlaylistById_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistIdNonExist = "99999";
        //act
        //assert
        mvc.perform(get("/api/playlists/" + playlistIdNonExist).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void givenExistingUserIdAndPlaylistName_whenCreateEmptyPlaylist_thenReturnEmptyPlaylist() throws Exception {
        //arrange
        String userId = String.valueOf(user.getId());
        String playlistName = "newPlaylistName";
        //act
        MvcResult result = mvc.perform(post("/api/playlists/user/" + userId + "?playlistName=" + playlistName).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String jsonContent = result.getResponse().getContentAsString();
        PlaylistDTO playlistDto = objectMapper.readValue(jsonContent, PlaylistDTO.class);
        Playlist playlist = playlistRepository.findById(playlistDto.getId()).get();
        List<Playlist> allPlaylists = playlistRepository.findAll();
        //assert
        assertAll(
                () -> assertEquals(3, allPlaylists.size()),
                () -> assertEquals("newPlaylistName", playlist.getName()),
                () -> assertEquals("username", playlist.getUser().getUsername()),
                () -> assertEquals(0, playlist.getPlaylistVideos().size())
        );
    }

    @Test
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
