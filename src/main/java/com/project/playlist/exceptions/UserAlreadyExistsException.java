package com.project.playlist.exceptions;

import jakarta.persistence.EntityExistsException;

public class UserAlreadyExistsException extends EntityExistsException {
    public UserAlreadyExistsException(String username) {
        super("User with username:" + username + " already exist!");
    }
}
