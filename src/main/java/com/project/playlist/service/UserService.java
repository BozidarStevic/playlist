package com.project.playlist.service;


import com.project.playlist.dto.UserRequest;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.mapper.UserMapper;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(UserRequest userRequest) {

        User existingUser = userRepository.findByUsername(userRequest.getUsername());
        if (existingUser == null) {
            User user = UserMapper.INSTANCE.fromUserRequest(userRequest);
//            String encodedPassword = passwordEncoder.encode(user.getPassword());
//            user.setPassword(encodedPassword);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        return userOptional.get();
    }
}
