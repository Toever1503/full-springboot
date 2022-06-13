package com.repositories;

import com.entities.Address;
import com.entities.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface IUserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByUserName(String username);

    Optional<UserEntity> findByMainAddress(Long id);
    Optional<UserEntity> findUserEntityByUserNameOrEmail(String username, String email);
    //Get all userId
    @Query("select u.id from UserEntity u")
    List<Long> getAllId();
}
