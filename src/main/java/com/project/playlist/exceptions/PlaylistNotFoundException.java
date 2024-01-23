package com.project.playlist.exceptions;

public class PlaylistNotFoundException extends RuntimeException {

    public PlaylistNotFoundException(String message) {
        super(message);
    }

    public PlaylistNotFoundException(Long playlistId) {
        super("Playlist with id:" + playlistId + " not found!");
    }
}