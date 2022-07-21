package com.repositories;

import com.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByUserName(String username);

    Optional<UserEntity> findByMainAddress(Long id);

    UserEntity findByUserName(String username);

    UserEntity findByEmail(String email);

    //Get all userId
    @Query("select u.id from UserEntity u")
    List<Long> getAllId();

    UserEntity findByPhone(String phone);

    @Query(value = "select u.user_id from tbl_user as u join user_role as ur on ur.user_id=u.user_id join tbl_role as r on r.role_id = ur.role_id  where r.role_name = ?1", nativeQuery = true)
    List<Long> getAllIdsByRole(String role);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String userName, String userName1);

    Optional<List<UserEntity>> findAllByIdIsIn(List<Long> ids);
}
