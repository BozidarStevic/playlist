package com.project.playlist.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class VideoNotFoundException extends EntityNotFoundException {
    public VideoNotFoundException(Long videoId) {
        super("Video with id:" + videoId + " not found!");
    }
}
