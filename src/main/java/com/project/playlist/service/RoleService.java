package com.project.playlist.service;

import com.project.playlist.model.Role;

public interface RoleService {
    Role getRoleByName(String name);
    boolean existsRoleByName(String roleName);
    Role createRole(Role role);
}
