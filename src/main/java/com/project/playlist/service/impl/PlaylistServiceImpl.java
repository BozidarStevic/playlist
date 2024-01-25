package com.project.playlist.service.impl;

import com.project.playlist.exceptions.PlaylistNotFoundException;
import com.project.playlist.model.Playlist;
import com.project.playlist.model.User;
import com.project.playlist.repository.PlaylistRepository;
import com.project.playlist.service.PlaylistService;
import com.project.playlist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserService userService;

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
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        if (playlistOptional.isEmpty()) {
            throw new PlaylistNotFoundException(playlistId);
        }
        return playlistOptional.get();
    }
}
