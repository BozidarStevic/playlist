package com.project.playlist.exceptions;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(String message) {
        super(message);
    }

    public VideoNotFoundException(Long videoId) {
        super("Video with id:" + videoId + " not found!");
    }
}
