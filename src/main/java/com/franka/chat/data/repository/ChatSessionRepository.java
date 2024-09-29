package com.franka.chat.data.repository;

import com.franka.chat.data.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    @Query(value = "select s from ChatSession s where " +
            "s.chatUserIp = :userIp and " +
            "s.chatUser.name = :username and " +
            "s.status = 'ACTIVE'")
    List<ChatSession> findForAuthentication(
            @Param("username") String username,
            @Param("userIp") String userIp);
}
