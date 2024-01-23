package com.project.playlist.service;

import com.project.playlist.model.Playlist;
import com.project.playlist.model.User;
import com.project.playlist.repository.PlaylistRepository;
import com.project.playlist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistService {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private UserRepository userRepository;

    public Playlist createEmptyPlaylist(Long userId, String playlistName) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        Playlist playlist = new Playlist();
        playlist.setName(playlistName);
        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }

    public Playlist getPlaylistById(Long playlistId) {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        return playlistOptional.orElse(null);
    }
}
