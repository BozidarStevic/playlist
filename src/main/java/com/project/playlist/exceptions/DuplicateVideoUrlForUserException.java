package com.project.playlist.exceptions;

import jakarta.persistence.EntityExistsException;

public class DuplicateVideoUrlForUserException extends EntityExistsException {
    public DuplicateVideoUrlForUserException(String url, Long userId) {
        super("User with id:" + userId + " already created video with url: " + url);
    }
}
