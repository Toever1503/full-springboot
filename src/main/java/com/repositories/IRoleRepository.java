package com.repositories;

import com.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {
    RoleEntity findRoleEntityByRoleId(Long id);

    RoleEntity findRoleEntityByRoleName(String roleName);
}
