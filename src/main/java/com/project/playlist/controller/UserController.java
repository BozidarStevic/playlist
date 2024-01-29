package com.project.playlist.controller;

import com.project.playlist.dto.UserDTO;
import com.project.playlist.dto.UserRequest;
import com.project.playlist.mapper.UserMapper;
import com.project.playlist.model.User;
import com.project.playlist.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserDTO registerNewUser(@RequestBody UserRequest userRequest) {
        User newUser = userService.registerUser(userRequest);
        return UserMapper.INSTANCE.toUserDTO(newUser);
    }

}
