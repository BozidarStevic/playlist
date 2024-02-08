package com.project.playlist.service.impl;


import com.project.playlist.dto.UserRequest;
import com.project.playlist.exceptions.UserAlreadyExistsException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.mapper.UserMapper;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(UserRequest userRequest) {
        if (userRequest == null) throw new IllegalArgumentException();
        userRepository.findByUsername(userRequest.getUsername())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistsException(userRequest.getUsername());
                });
        User user = UserMapper.INSTANCE.fromUserRequest(userRequest);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
