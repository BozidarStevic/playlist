package com.project.playlist.service.impl;


import com.project.playlist.dto.UserRequest;
import com.project.playlist.exceptions.UserAlreadyExistsException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.mapper.UserMapper;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(UserRequest userRequest) {
        Optional<User> userOptional = userRepository.findByUsername(userRequest.getUsername());
        if (userOptional.isEmpty()) {
            User user = UserMapper.INSTANCE.fromUserRequest(userRequest);
            return userRepository.save(user);
        } else {
            throw new UserAlreadyExistsException(userRequest.getUsername());
        }
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return userOptional.get();
    }
}
