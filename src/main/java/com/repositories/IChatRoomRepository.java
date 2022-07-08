package com.repositories;

import com.entities.ChatRoomEntity;
import com.entities.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.UUID;

public interface IChatRoomRepository extends JpaRepository<ChatRoomEntity, String> {
    List<ChatRoomEntity> findAllByUserEntitiesContains(UserEntity userEntity);

    @Query("select c from ChatRoomEntity c where c.userEntities.size = 1")
    List<ChatRoomEntity> getAllAvailableRoom(Pageable pageable);

    List<ChatRoomEntity> getAllByUserEntitiesContains(UserEntity userEntity, Pageable pageable);
}
