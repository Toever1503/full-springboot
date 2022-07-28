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
    @Query(value = "SELECT u.user_id FROM marketplace_cy_2.tbl_user u \n" +
            "join user_role r on u.user_id = r.user_id \n" +
            "join tbl_role as rl on rl.role_id = r.role_id\n" +
            "where r.role_id = 1 and u.user_id not in (\n" +
            "\tSELECT u1.user_id FROM marketplace_cy_2.tbl_user u1 \n" +
            "\tjoin user_role r1 on u1.user_id = r1.user_id \n" +
            "\tjoin tbl_role as rl1 on rl1.role_id = r1.role_id\n" +
            "\twhere r1.role_id = 2\n" +
            "\t)", nativeQuery = true)
    List<Long> getAllId();

    UserEntity findByPhone(String phone);

    @Query(value = "select u.user_id from tbl_user as u join user_role as ur on ur.user_id=u.user_id join tbl_role as r on r.role_id = ur.role_id  where r.role_name = ?1", nativeQuery = true)
    List<Long> getAllIdsByRole(String role);

    Optional<UserEntity> findUserEntityByUserNameOrEmail(String userName, String userName1);

    Optional<List<UserEntity>> findAllByIdIsIn(List<Long> ids);
}
