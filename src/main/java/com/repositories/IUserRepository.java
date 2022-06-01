package com.repositories;

import com.entities.UserEntity;
import org.apache.catalina.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface IUserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByUserName(String username);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String username, String email);


}
