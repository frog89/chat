package com.franka.chat.data.repository;

import com.franka.chat.data.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
  @Query(value = "select c from Chat c where " +
                 "(c.firstChatSession.id = :firstSessionId and " +
                 "c.secondChatSession.id = :secondSessionId) or " +
                 "(c.firstChatSession.id = :secondSessionId and " +
                 "c.secondChatSession.id = :firstSessionId)")
  List<Chat> findChatBySessionIds(@Param("firstSessionId") Long firstSessionId,
                             @Param("secondSessionId") Long secondSessionId);

  @Query(value = "select c from Chat c where " +
                 "c.firstChatSession.id = :sessionId or " +
                 "c.secondChatSession.id = :sessionId")
  List<Chat> findChatsBySessionId(@Param("sessionId") Long sessionId);

  @Query(value = "select c from Chat c " +
                 "where (c.firstChatSession.id = :currentSessionId and c.isHiddenForFirstChatSession = ChatHidden.Y) " +
                 "or (c.secondChatSession.id = :currentSessionId and c.isHiddenForSecondChatSession = ChatHidden.Y)")
  List<Chat> findHiddenChats(@Param("currentSessionId") Long currentSessionId);

}
