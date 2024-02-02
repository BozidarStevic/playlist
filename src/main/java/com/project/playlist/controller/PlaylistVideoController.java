package com.project.playlist.controller;

import com.project.playlist.dto.VideoDTO;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Video;
import com.project.playlist.service.PlaylistVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/playlist-videos")
public class PlaylistVideoController {

    private final PlaylistVideoService playlistVideoService;

    public PlaylistVideoController(PlaylistVideoService playlistVideoService) {
        this.playlistVideoService = playlistVideoService;
    }

    @PostMapping("/playlist/{playlistId}/video/{videoId}")
    public void addVideoToPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        playlistVideoService.addVideoToPlaylist(playlistId, videoId);
    }

    @DeleteMapping("/playlist/{playlistId}/video/{videoId}")
    public void removeVideoFromPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        playlistVideoService.removeVideoFromPlaylist(playlistId, videoId);
    }

    @PutMapping("/playlist/{playlistId}/videos/order")
    public void changeVideoOrder(@PathVariable Long playlistId, @RequestParam int fromOrderNo, @RequestParam int toOrderNo) {
        playlistVideoService.changeVideoOrder(playlistId, fromOrderNo, toOrderNo);
    }

    @GetMapping("/playlist/{playlistId}/videos")
    public List<VideoDTO> getSortedVideosForPlaylist(@PathVariable Long playlistId) {
        List<Video> videos = playlistVideoService.getSortedVideosForPlaylist(playlistId);
        return VideoMapper.INSTANCE.toDTOList(videos);
    }
}
