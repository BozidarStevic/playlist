package com.project.playlist.controller;

import com.project.playlist.dto.VideoDTO;
import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.VideoNotFoundException;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Video;
import com.project.playlist.service.PlaylistVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/playlist-videos")
public class PlaylistVideoController {

    @Autowired
    private PlaylistVideoService playlistVideoService;

    @PostMapping("/playlists/{playlistId}/videos/{videoId}")
    public ResponseEntity<Void> addVideoToPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        try {
            playlistVideoService.addVideoToPlaylist(playlistId, videoId);
            return ResponseEntity.ok().build();
        } catch (PlaylistNotFoundException | VideoNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/playlists/{playlistId}/videos/{videoId}")
    public ResponseEntity<Void> removeVideoFromPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        try {
            playlistVideoService.removeVideoFromPlaylist(playlistId, videoId);
            return ResponseEntity.ok().build();
        } catch (PlaylistNotFoundException | VideoNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/playlists/{playlistId}/videos/order")
    public ResponseEntity<Void> changeVideoOrder(@PathVariable Long playlistId, @RequestParam int fromOrderNo, @RequestParam int toOrderNo) {
        try {
            playlistVideoService.changeVideoOrder(playlistId, fromOrderNo, toOrderNo);
            return ResponseEntity.ok().build();
        } catch (PlaylistNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/playlists/{playlistId}/videos/sort")
    public ResponseEntity<List<VideoDTO>> getSortedVideosForPlaylist(@PathVariable Long playlistId) {
        try {
            List<Video> videos = playlistVideoService.getSortedVideosForPlaylist(playlistId);
            List<VideoDTO> videoDTOs = VideoMapper.INSTANCE.toDTOList(videos);
            return ResponseEntity.ok().body(videoDTOs);
        } catch (PlaylistNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
