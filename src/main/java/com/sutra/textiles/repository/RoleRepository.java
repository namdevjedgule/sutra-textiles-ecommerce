package com.sutra.textiles.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sutra.textiles.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    Optional<Role> findByRoleNameAndIsActiveTrue(String roleName);
}
