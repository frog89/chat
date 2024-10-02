package com.franka.chat.data.repository;

import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    @Query(value = "select s from ChatSession s where " +
      "(s.userName = :userName or s.userName like concat('%', :userName, '-%')) and " +
      "s.userKind = :userKind and " +
      "s.status = 'ACTIVE'")
    List<ChatSession> findForAuthentication(
      @Param("userName") String userName,
      @Param("userKind") ChatUserKind userKind);

    @Query(value = "select s from ChatSession s " +
      "where lower(s.userName) like lower(concat('%', :userName, '%'))")
    List<ChatSession> searchUserNameIgnoreCase(@Param("userName") String userName);

    @Query(value = "select s from ChatSession s " +
                   "where s.userName = :userName")
    ChatSession findByName(@Param("userName") String userName);
}
