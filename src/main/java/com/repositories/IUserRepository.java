package com.repositories;

import com.entities.UserEntity;
import org.apache.catalina.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface IUserRepository extends JpaRepository<UserEntity,Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByUserName(String username);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String username, String email);

    @Query("select u.id from UserEntity u")
    List<Long> getAllId();
}
