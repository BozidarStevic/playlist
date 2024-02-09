package com.project.playlist.controller;

import com.project.playlist.dto.PlaylistDTO;
import com.project.playlist.mapper.PlaylistCustomMapper;
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
    private final PlaylistCustomMapper playlistCustomMapper;

    public PlaylistController(PlaylistService playlistService, PlaylistCustomMapper playlistCustomMapper) {
        this.playlistService = playlistService;
        this.playlistCustomMapper = playlistCustomMapper;
    }

    @PostMapping("/user/{userId}")
    public PlaylistDTO createEmptyPlaylist(@PathVariable Long userId, @RequestParam @NotBlank String playlistName) {
        Playlist playlist = playlistService.createEmptyPlaylist(userId, playlistName);
        return playlistCustomMapper.toDTO(playlist);
    }

    @GetMapping("/{playlistId}")
    public PlaylistDTO getPlaylistById(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        return playlistCustomMapper.toDTO(playlist);
    }

}
