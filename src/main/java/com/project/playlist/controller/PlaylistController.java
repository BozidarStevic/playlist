package com.project.playlist.controller;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.mapper.PlaylistMapper;
import com.project.playlist.model.Playlist;
import com.project.playlist.service.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<PlaylistDTO> createEmptyPlaylist(@PathVariable Long userId, @RequestParam String name) {
        try {
            Playlist playlist = playlistService.createEmptyPlaylist(userId, name);
            PlaylistDTO playlistDTO = PlaylistMapper.INSTANCE.toDTO(playlist);
            return ResponseEntity.created(URI.create("/api/playlists/" + playlist.getId())).body(playlistDTO);
        } catch (UserNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long playlistId) {
        try {
            Playlist playlist = playlistService.getPlaylistById(playlistId);
            PlaylistDTO playlistDTO = PlaylistMapper.INSTANCE.toDTO(playlist);
            return ResponseEntity.ok(playlistDTO);
        } catch (PlaylistNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
