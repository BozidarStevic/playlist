package com.project.playlist.service;

import com.project.playlist.dto.UserRequest;
import com.project.playlist.model.User;

public interface UserService {
    User registerUser(UserRequest userRequest);
    User getUserById(Long userId);
}
