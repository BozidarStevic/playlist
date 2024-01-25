package com.project.playlist.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("User with username:" + username + " already exist!");
    }
}
