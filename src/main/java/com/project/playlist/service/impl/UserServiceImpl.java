package com.project.playlist.service.impl;


import com.project.playlist.dto.UserRequest;
import com.project.playlist.exceptions.UserAlreadyExistsException;
import com.project.playlist.exceptions.UserNotFoundException;
import com.project.playlist.mapper.UserMapper;
import com.project.playlist.model.Role;
import com.project.playlist.model.User;
import com.project.playlist.repository.UserRepository;
import com.project.playlist.service.RoleService;
import com.project.playlist.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleService roleService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public User registerUser(UserRequest userRequest) {
        if (userRequest == null) throw new IllegalArgumentException();
        userRepository.findByUsername(userRequest.getUsername())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistsException(userRequest.getUsername());
                });
        User user = UserMapper.INSTANCE.fromRequest(userRequest);
        if (!roleService.existsRoleByName("ROLE_USER")) {
            roleService.createRole(Role
                    .builder()
                    .name("ROLE_USER")
                    .build()
            );
        }
        Role role = roleService.getRoleByName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
