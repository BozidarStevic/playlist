package com.project.playlist.integration_testing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.playlist.dto.VideoForPlaylistDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class PlaylistVideoControllerIntegrationTest extends MySqlIntegrationTest {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private PlaylistVideoRepository playlistVideoRepository;
    @Autowired
    PlaylistRepository playlistRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private User user, testUser;
    private Video video1, video2, video3, video4, video5, video6New;
    private Playlist playlist1, playlist2;
    private PlaylistVideo pv1, pv2, pv3, pv4, pv5;
    private Role testRole;

    @BeforeEach
    void setUp() throws Exception {
        user = userRepository.save(User.builder().username("username").email("email@gmail.com").password("pass1").build());
        video1 = videoRepository.save(Video.builder().url("http://www.example.com/video1").name("videoName1").description("videoDescription1").user(user).build());
        video2 = videoRepository.save(Video.builder().url("http://www.example.com/video2").name("videoName2").description("videoDescription2").user(user).build());
        video3 = videoRepository.save(Video.builder().url("http://www.example.com/video3").name("videoName3").description("videoDescription3").user(user).build());
        video4 = videoRepository.save(Video.builder().url("http://www.example.com/video4").name("videoName4").description("videoDescription4").user(user).build());
        video5 = videoRepository.save(Video.builder().url("http://www.example.com/video5").name("videoName5").description("videoDescription5").user(user).build());
        video6New = videoRepository.save(Video.builder().url("http://www.example.com/video6New").name("videoName6New").description("videoDescription6New").user(user).build());
        playlist1 = playlistRepository.save(Playlist.builder().user(user).name("playlistName1").build());
        pv1 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video1).orderNo(1).build());
        pv2 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video2).orderNo(2).build());
        pv3 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video3).orderNo(3).build());
        pv4 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video4).orderNo(4).build());
        pv5 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist1).video(video5).orderNo(5).build());
        playlist1.setPlaylistVideos(new ArrayList<>(List.of(pv1, pv2, pv3, pv4, pv5)));
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
    public void givenUnsortedPlaylistAndExistingPlaylistId_whenGetSortedVideosForPlaylist_thenReturnSortedVideosForPlaylist() throws Exception {
        //arrange
        pv1.setOrderNo(5);
        pv2.setOrderNo(4);
        pv3.setOrderNo(3);
        pv4.setOrderNo(2);
        pv5.setOrderNo(1);
        playlistVideoRepository.saveAll(new ArrayList<>(List.of(pv1, pv2, pv3, pv4, pv5)));
        playlist1.setPlaylistVideos(new ArrayList<>(List.of(pv1, pv2, pv3, pv4, pv5)));
        playlist1 = playlistRepository.save(playlist1);
        String playlistId = String.valueOf(playlist1.getId());
        //act
        MvcResult result = mvc.perform(get("/api/playlist-videos/playlist/" + playlistId + "/videos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ArrayList<VideoForPlaylistDTO> videoDtoList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        //assert
        assertAll(
                () -> assertEquals(5, videoDtoList.size()),
                () -> assertEquals("videoName5", videoDtoList.getFirst().getName()),
                () -> assertEquals(1, videoDtoList.getFirst().getOrderNo()),
                () -> assertEquals("videoName4", videoDtoList.get(1).getName()),
                () -> assertEquals(2, videoDtoList.get(1).getOrderNo()),
                () -> assertEquals("videoName3", videoDtoList.get(2).getName()),
                () -> assertEquals(3, videoDtoList.get(2).getOrderNo()),
                () -> assertEquals("videoName2", videoDtoList.get(3).getName()),
                () -> assertEquals(4, videoDtoList.get(3).getOrderNo()),
                () -> assertEquals("videoName1", videoDtoList.get(4).getName()),
                () -> assertEquals(5, videoDtoList.get(4).getOrderNo())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingPlaylistId_whenGetSortedVideosForPlaylist_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistId = "99999";
        //act
        //assert
        mvc.perform(get("/api/playlist-videos/playlist/" + playlistId + "/videos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullPlaylistId_whenGetSortedVideosForPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = null;
        //act
        //assert
        mvc.perform(get("/api/playlist-videos/playlist/" + playlistId + "/videos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistIdAndVideoIdNotInPlaylist_whenAddVideoToPlaylist_thanAddedVideoToPlaylist() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = String.valueOf(video6New.getId());
        //act
        mvc.perform(post("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Playlist playlist = playlistRepository.findById(playlist1.getId()).get();
        //assert
        assertAll(
                () -> assertEquals(6, playlist.getPlaylistVideos().size()),
                () -> assertEquals(video6New.getId(), playlist.getPlaylistVideos().getLast().getVideo().getId()),
                () -> assertEquals(6, playlist.getPlaylistVideos().getLast().getOrderNo())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistIdAndVideoIdAlreadyInPlaylist_whenAddVideoToPlaylist_thenExpectConflictStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = String.valueOf(video4.getId());
        //act
        //assert
        mvc.perform(post("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingPlaylistId_whenAddVideoToPlaylist_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistId = "99999";
        String videoId = String.valueOf(video6New.getId());
        //act
        //assert
        mvc.perform(post("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullPlaylistId_whenAddVideoToPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = null;
        String videoId = String.valueOf(video6New.getId());
        //act
        //assert
        mvc.perform(post("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingVideoId_whenAddVideoToPlaylist_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = "99999";
        //act
        //assert
        mvc.perform(post("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullVideoId_whenAddVideoToPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = null;
        //act
        //assert
        mvc.perform(post("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistIdAndVideoIdInPlaylist_whenRemoveVideoFromPlaylist_thanRemovedVideoFromPlaylist() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = String.valueOf(video3.getId());
        //act
        mvc.perform(delete("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Playlist playlist = playlistRepository.findById(playlist1.getId()).get();
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistIdOrderByOrderNo(playlist1.getId());
        //assert
        assertAll(
                () -> assertEquals(4, playlist.getPlaylistVideos().size()),
                () -> assertFalse(playlist.getPlaylistVideos().stream().anyMatch(pv -> pv.getVideo().getId().equals(video3.getId()))),
                () -> assertEquals("videoName1", playlist.getPlaylistVideos().get(0).getVideo().getName()),
                () -> assertEquals(1, playlist.getPlaylistVideos().get(0).getOrderNo()),
                () -> assertEquals("videoName2", playlist.getPlaylistVideos().get(1).getVideo().getName()),
                () -> assertEquals(2, playlist.getPlaylistVideos().get(1).getOrderNo()),
                () -> assertEquals("videoName4", playlist.getPlaylistVideos().get(2).getVideo().getName()),
                () -> assertEquals(3, playlist.getPlaylistVideos().get(2).getOrderNo()),
                () -> assertEquals("videoName5", playlist.getPlaylistVideos().get(3).getVideo().getName()),
                () -> assertEquals(4, playlist.getPlaylistVideos().get(3).getOrderNo()),
                () -> assertTrue(playlistVideoRepository.findByPlaylistIdAndVideoId(playlist1.getId(), video3.getId()).isEmpty()),
                () -> assertEquals(4, playlistVideoList.size()),
                () -> assertEquals("videoName1", playlistVideoList.get(0).getVideo().getName()),
                () -> assertEquals("videoName2", playlistVideoList.get(1).getVideo().getName()),
                () -> assertEquals("videoName4", playlistVideoList.get(2).getVideo().getName()),
                () -> assertEquals("videoName5", playlistVideoList.get(3).getVideo().getName())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistIdAndVideoIdNotInPlaylist_whenRemoveVideoFromPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = String.valueOf(video6New.getId());
        //act
        //assert
        mvc.perform(delete("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullPlaylistId_whenRemoveVideoFromPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = null;
        String videoId = String.valueOf(video3.getId());
        //act
        //assert
        mvc.perform(delete("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullVideoId_whenRemoveVideoFromPlaylist_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = null;
        //act
        //assert
        mvc.perform(delete("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingPlaylistId_whenRemoveVideoFromPlaylist_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistId = "99999";
        String videoId = String.valueOf(video3.getId());
        //act
        //assert
        mvc.perform(delete("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingVideoId_whenRemoveVideoFromPlaylist_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String videoId = "99999";
        //act
        //assert
        mvc.perform(delete("/api/playlist-videos/playlist/" + playlistId + "/video/" + videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistIdAndFromOrderNoGreaterThanToOrderNo_whenChangeVideoOrder_thanChangedVideoOrderInPlaylist() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = "4";
        String toOrderNo = "2";
        //act
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Playlist playlist = playlistRepository.findById(playlist1.getId()).get();
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistIdOrderByOrderNo(playlist1.getId());
        //assert
        assertAll(
                () -> assertEquals(5, playlist.getPlaylistVideos().size()),
                () -> assertEquals(5, playlistVideoList.size()),
                () -> assertEquals("videoName1", playlistVideoList.get(0).getVideo().getName()),
                () -> assertEquals("videoName4", playlistVideoList.get(1).getVideo().getName()),
                () -> assertEquals("videoName2", playlistVideoList.get(2).getVideo().getName()),
                () -> assertEquals("videoName3", playlistVideoList.get(3).getVideo().getName()),
                () -> assertEquals("videoName5", playlistVideoList.get(4).getVideo().getName())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenExistingPlaylistIdAndFromOrderNoLessThanToOrderNo_whenChangeVideoOrder_thanChangedVideoOrderInPlaylist() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = "2";
        String toOrderNo = "4";
        //act
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Playlist playlist = playlistRepository.findById(playlist1.getId()).get();
        List<PlaylistVideo> playlistVideoList = playlistVideoRepository.findByPlaylistIdOrderByOrderNo(playlist1.getId());
        //assert
        assertAll(
                () -> assertEquals(5, playlist.getPlaylistVideos().size()),
                () -> assertEquals(5, playlistVideoList.size()),
                () -> assertEquals("videoName1", playlistVideoList.get(0).getVideo().getName()),
                () -> assertEquals("videoName3", playlistVideoList.get(1).getVideo().getName()),
                () -> assertEquals("videoName4", playlistVideoList.get(2).getVideo().getName()),
                () -> assertEquals("videoName2", playlistVideoList.get(3).getVideo().getName()),
                () -> assertEquals("videoName5", playlistVideoList.get(4).getVideo().getName())
        );
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullPlaylistId_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = null;
        String fromOrderNo = "4";
        String toOrderNo = "2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullFromOrderNo_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = null;
        String toOrderNo = "2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNullToOrderNo_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = "4";
        String toOrderNo = null;
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenNonExistingPlaylistId_whenChangeVideoOrder_thenExpectNotFoundStatus() throws Exception {
        //arrange
        String playlistId = "99999";
        String fromOrderNo = "4";
        String toOrderNo = "2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenEmptyPlaylistId_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist2.getId());
        String fromOrderNo = "4";
        String toOrderNo = "2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenInvalidFromOrderNo_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = "9999";
        String toOrderNo = "2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenInvalidToOrderNo_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = "4";
        String toOrderNo = "-2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenEqualOrderNumbers_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        String playlistId = String.valueOf(playlist1.getId());
        String fromOrderNo = "2";
        String toOrderNo = "2";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithUserDetails(value = "username1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void givenPlaylistWithOneVideo_whenChangeVideoOrder_thenExpectBadRequestStatus() throws Exception {
        //arrange
        PlaylistVideo pv7 = playlistVideoRepository.save(PlaylistVideo.builder().playlist(playlist2).video(video1).orderNo(1).build());
        playlist2.setPlaylistVideos(new ArrayList<>(List.of(pv7)));
        playlistRepository.save(playlist2);
        String playlistId = String.valueOf(playlist2.getId());
        String fromOrderNo = "4";
        String toOrderNo = "1";
        //act
        //assert
        mvc.perform(put("/api/playlist-videos/playlist/" + playlistId + "/videos/order?fromOrderNo="+ fromOrderNo + "&toOrderNo=" + toOrderNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

}
