package com.project.playlist.exceptions;

import jakarta.persistence.EntityExistsException;

public class VideoAlreadyInPlaylistException extends EntityExistsException {
    public VideoAlreadyInPlaylistException(Long videoId, Long playlistId) {
        super("Video with id: " + videoId + " already in playlist with id: " + playlistId);
    }
}
