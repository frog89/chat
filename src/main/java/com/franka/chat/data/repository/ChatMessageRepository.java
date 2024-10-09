package com.franka.chat.data.repository;

import com.franka.chat.data.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

  @Query(value = "select m from ChatMessage m where " +
                 "m.chat.id = :chatId " +
                 "order by m.id desc")
  List<ChatMessage> findByChatId(@Param("chatId") Long chatId);
}
