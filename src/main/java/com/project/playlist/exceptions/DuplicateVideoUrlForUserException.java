package com.project.playlist.exceptions;

public class DuplicateVideoUrlForUserException extends RuntimeException {
    public DuplicateVideoUrlForUserException(String url, Long userId) {
        super("User with id:" + userId + " already created video with url: " + url);
    }
}
