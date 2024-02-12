package com.project.playlist.controller;

import com.project.playlist.dto.VideoForPlaylistDTO;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Video;
import com.project.playlist.service.PlaylistVideoService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/playlist-videos")
public class PlaylistVideoController {

    private final PlaylistVideoService playlistVideoService;

    public PlaylistVideoController(PlaylistVideoService playlistVideoService) {
        this.playlistVideoService = playlistVideoService;
    }

    @PostMapping("/playlist/{playlistId}/video/{videoId}")
    public void addVideoToPlaylist(@PathVariable @NotNull Long playlistId, @PathVariable @NotNull Long videoId) {
        playlistVideoService.addVideoToPlaylist(playlistId, videoId);
    }

    @DeleteMapping("/playlist/{playlistId}/video/{videoId}")
    public void removeVideoFromPlaylist(@PathVariable @NotNull Long playlistId, @NotNull @PathVariable Long videoId) {
        playlistVideoService.removeVideoFromPlaylist(playlistId, videoId);
    }

    @PutMapping("/playlist/{playlistId}/videos/order")
    public void changeVideoOrder(@PathVariable @NotNull Long playlistId, @RequestParam @NotNull int fromOrderNo, @RequestParam @NotNull int toOrderNo) {
        playlistVideoService.changeVideoOrder(playlistId, fromOrderNo, toOrderNo);
    }

    @GetMapping("/playlist/{playlistId}/videos")
    public List<VideoForPlaylistDTO> getSortedVideosForPlaylist(@PathVariable @NotNull Long playlistId) {
        List<Video> videos = playlistVideoService.getSortedVideosForPlaylist(playlistId);
        return videos.stream()
                .map(video -> VideoMapper.INSTANCE.videoToVideoForPlaylistDTO(video, videos.indexOf(video) + 1))
                .collect(Collectors.toList());
    }
}
