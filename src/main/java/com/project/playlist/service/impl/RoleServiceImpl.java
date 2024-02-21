package com.project.playlist.service.impl;

import com.project.playlist.exceptions.RoleNotFoundException;
import com.project.playlist.model.Role;
import com.project.playlist.repository.RoleRepository;
import com.project.playlist.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    public Role getRoleByName(String roleName){
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));
    }

    @Override
    public boolean existsRoleByName(String roleName) {
        return roleRepository.existsByName(roleName);
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}
