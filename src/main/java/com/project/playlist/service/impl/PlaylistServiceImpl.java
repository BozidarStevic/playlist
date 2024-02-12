package com.project.playlist.service.impl;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.User;
import com.project.playlist.repository.PlaylistRepository;
import com.project.playlist.service.PlaylistService;
import com.project.playlist.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final UserService userService;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, UserService userService) {
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    @Override
    public Playlist createEmptyPlaylist(Long userId, String playlistName) {
        User user = userService.getUserById(userId);
        Playlist playlist = new Playlist();
        playlist.setName(playlistName);
        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }
    @Override
    public Playlist getPlaylistById(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException(playlistId));
    }

    @Override
    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }
}
