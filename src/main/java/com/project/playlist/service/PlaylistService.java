package com.project.playlist.service;

import com.project.playlist.model.Playlist;

public interface PlaylistService {
    Playlist createEmptyPlaylist(Long userId, String playlistName);
    Playlist getPlaylistById(Long playlistId);

    Playlist savePlaylist(Playlist playlist);
}
