package com.project.playlist.exceptions;

public class PlaylistNotFoundException extends RuntimeException {
    public PlaylistNotFoundException(Long playlistId) {
        super("Playlist with id:" + playlistId + " not found!");
    }
}