package com.project.playlist.exceptions;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException(Long videoId) {
        super("Video with id:" + videoId + " not found!");
    }
}
