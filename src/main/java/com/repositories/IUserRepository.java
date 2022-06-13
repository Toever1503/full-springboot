package com.repositories;

import com.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface IUserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByUserName(String username);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String username, String email);
    //Get all userId
    @Query("select u.id from UserEntity u")
    List<Long> getAllId();

    UserEntity findByPhone(String phone);
}
