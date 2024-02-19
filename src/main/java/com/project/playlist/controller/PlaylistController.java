package com.project.playlist.controller;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.mapper.PlaylistMapper;
import com.project.playlist.model.Playlist;
import com.project.playlist.service.PlaylistService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping("/user/{userId}")
    public PlaylistDTO createEmptyPlaylist(@PathVariable Long userId, @RequestParam @NotBlank String playlistName) {
        Playlist playlist = playlistService.createEmptyPlaylist(userId, playlistName);
        return PlaylistMapper.INSTANCE.toDTO(playlist);
    }

    @GetMapping("/{playlistId}")
    public PlaylistDTO getPlaylistById(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        return PlaylistMapper.INSTANCE.toDTO(playlist);
    }

}
