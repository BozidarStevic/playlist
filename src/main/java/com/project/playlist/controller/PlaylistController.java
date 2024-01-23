package com.project.playlist.controller;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.dto.PlaylistVideoDTO;
import com.project.playlist.dto.VideoDTO;
import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.VideoNotFoundException;
import com.project.playlist.mapper.PlaylistMapper;
import com.project.playlist.mapper.VideoMapper;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.Video;
import com.project.playlist.service.PlaylistVideoService;
import com.project.playlist.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private PlaylistVideoService playlistVideoService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<PlaylistDTO> createEmptyPlaylist(@PathVariable Long userId, @RequestParam String name) {
        Playlist playlist = playlistService.createEmptyPlaylist(userId, name);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }
        PlaylistDTO playlistDTO = PlaylistMapper.INSTANCE.toDTO(playlist);
        return ResponseEntity.created(null).body(playlistDTO);
    }

    @PostMapping("/{playlistId}/videos/{videoId}")
    public ResponseEntity<Void> addVideoToPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        try {
            playlistVideoService.addVideoToPlaylist(playlistId, videoId);
            return ResponseEntity.ok().build();
        } catch (PlaylistNotFoundException | VideoNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{playlistId}/videos/{videoId}")
    public ResponseEntity<String> removeVideoFromPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        try {
            playlistVideoService.removeVideoFromPlaylist(playlistId, videoId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{playlistId}/videos/order")
    public ResponseEntity<String> changeVideoOrder(@PathVariable Long playlistId, @RequestParam int fromOrderNo, @RequestParam int toOrderNo) {
        try {
            playlistVideoService.changeVideoOrder(playlistId, fromOrderNo, toOrderNo);
            return ResponseEntity.ok().build();
        } catch (PlaylistNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{playlistId}/videos/sort")
    public ResponseEntity<List<VideoDTO>> getSortedVideosForPlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        if (playlist == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Video> videos = playlistVideoService.getSortedVideosForPlaylist(playlistId);
        if (videos != null && !videos.isEmpty()) {
            List<VideoDTO> videoDTOs = VideoMapper.INSTANCE.toDTOList(videos);
            return ResponseEntity.ok().body(videoDTOs);
        }
        return ResponseEntity.internalServerError().build();
    }



}
