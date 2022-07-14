package com.repositories;

import com.entities.chat.ChatRoomEntity;
import com.entities.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface IChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {


    Optional<ChatRoomEntity> findByCreatedById(Long userId);

}
