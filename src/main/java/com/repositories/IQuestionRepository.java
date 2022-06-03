package com.repositories;

import com.entities.QuestionEntity;
import com.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IQuestionRepository extends JpaRepository<QuestionEntity, Long>, JpaSpecificationExecutor<QuestionEntity> {
    Page<QuestionEntity> getQuestionEntitiesByCompatibleIsTrue(Pageable pageable);

    @Query("select c from QuestionEntity c,UserEntity u where c.createdBy = u and c.createdBy.id = ?1 and c.isCompatible = ?2")
    Page<QuestionEntity> getQuestionEntitiesByUIDandCompat(Long id,boolean compat, Pageable pageable);

    @Query("select c from QuestionEntity c where c.isCompatible=?1")
    Page<QuestionEntity> findAllWithCompatible(boolean compat, Pageable pageable);

    @Query("select c from QuestionEntity c,UserEntity u where c.createdBy = u and c.createdBy.id = ?1 and c.isCompatible = ?2 and c.status = ?3")
    Page<QuestionEntity> findAllQuestionByUID(Long uid, boolean compat, String status, Pageable pageable);

    @Query("SELECT distinct c.createdBy from QuestionEntity c")
    List<UserEntity> getAllAskedUserIDByStatus();

    @Query("select c from QuestionEntity c where c.category=?1")
    Page<QuestionEntity> findQuestionsByCategory(String category, Pageable pageable);

}
