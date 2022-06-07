package com.repositories;

import com.entities.QuestionEntity;
import com.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IQuestionRepository extends JpaRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {
    Page<QuestionEntity> getQuestionEntitiesByCompatibleIsTrue(Pageable pageable);
    //Get all question by user id and compatible
    @Query("select c from QuestionEntity c,UserEntity u where c.createdBy = u and c.createdBy.id = ?1 and c.isCompatible = ?2")
    Page<QuestionEntity> getQuestionEntitiesByUIDandCompat(Long id,boolean compat, Pageable pageable);
    //Get all question by compatible
    @Query("select c from QuestionEntity c where c.isCompatible=?1")
    Page<QuestionEntity> findAllWithCompatible(boolean compat, Pageable pageable);
    //Get all question by user id, compatible and status
    @Query("select c from QuestionEntity c,UserEntity u where c.createdBy = u and c.createdBy.id = ?1 and c.isCompatible = ?2 and c.status = ?3")
    Page<QuestionEntity> findAllQuestionByUID(Long uid, boolean compat, String status, Pageable pageable);
    //Get all asked user
    @Query("SELECT distinct c.createdBy from QuestionEntity c")
    List<UserEntity> getAllAskedUserIDByStatus();
    //Get all question by category
    @Query("select c from QuestionEntity c where c.category=?1")
    Page<QuestionEntity> findQuestionsByCategory(String category, Pageable pageable);
    //Get all question by category and userId
    @Query("select c from QuestionEntity c where c.category=?1 and c.createdBy.id = ?2")
    Page<QuestionEntity> findQuestionsByCategoryAndUserId(String category, Long userId, Pageable pageable);

    Optional<QuestionEntity> findByIdAndCreatedById(Long questionId, Long UserId);

}
