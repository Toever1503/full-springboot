package com.repositories;

import com.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findRoleEntityByRoleId(Long id);

    RoleEntity findRoleEntityByRoleName(String roleName);
}
