package com.project.playlist.controller;

import com.project.playlist.dto.UserDTO;
import com.project.playlist.dto.UserRequest;
import com.project.playlist.mapper.UserMapper;
import com.project.playlist.model.User;
import com.project.playlist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("users/register")
    public ResponseEntity<UserDTO> registerNewUser(@RequestBody UserRequest userRequest) {
        User newUser = userService.registerUser(userRequest);
        if (newUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        UserDTO userDTO = UserMapper.INSTANCE.toUserDTO(newUser);
        return ResponseEntity.ok().body(userDTO);
    }

}
