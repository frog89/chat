package com.franka.chat.data.repository;

import com.franka.chat.data.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    @Query(value = "select u from ChatUser u " +
            "where lower(u.name) like lower(concat('%', :searchTerm, '%'))")
    List<ChatUser> search(@Param("searchTerm") String searchTerm);
}
