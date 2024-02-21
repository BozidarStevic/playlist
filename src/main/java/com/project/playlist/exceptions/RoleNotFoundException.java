package com.project.playlist.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {
    public RoleNotFoundException(String roleName) {
        super("Role with name:" + roleName + " not found!");
    }
}
