package com.repositories;

import com.entities.MessageEntity;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IMessageRepository extends JpaRepository<MessageEntity,Long> {

    List<MessageEntity> findAllByChatRoomEntity_RoomId(String roomId, Pageable pageable);
}
