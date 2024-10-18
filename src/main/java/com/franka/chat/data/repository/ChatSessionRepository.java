package com.franka.chat.data.repository;

import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    @Query(value = "select s from ChatSession s where " +
      "s.userName = :userName and " +
      "s.userKind = :userKind and " +
      "s.status = 'ACTIVE'")
    List<ChatSession> findForAuthentication(
      @Param("userName") String userName,
      @Param("userKind") ChatUserKind userKind);

    @Query(value = "select s from ChatSession s " +
      "where s.id <> :currentSessionId and " +
      "lower(s.userName) like lower(concat('%', :filterUserName, '%')) " +
      "order by userName asc")
    List<ChatSession> findUserNameIgnoreCaseOrdered(@Param("currentSessionId") Long currentSessionId,
      @Param("filterUserName") String filterUserName);

    @Query(value = "select s from ChatSession s " +
                   "where s.id <> :currentSessionId " +
                   "order by s.userName asc")
    List<ChatSession> findAllOrdered(@Param("currentSessionId") Long currentSessionId);

    @Query(value = "select s from ChatSession s " +
                   "where s.userName = :userName")
    ChatSession findByName(@Param("userName") String userName);
}
