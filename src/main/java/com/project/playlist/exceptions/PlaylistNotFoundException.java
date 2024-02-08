package com.project.playlist.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class PlaylistNotFoundException extends EntityNotFoundException {
    public PlaylistNotFoundException(Long playlistId) {
        super("Playlist with id:" + playlistId + " not found!");
    }
}