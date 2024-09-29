package com.franka.chat.data.entity;

import com.franka.chat.data.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session")
public class ChatSession extends AbstractEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name="chat_user_id")
    private ChatUser chatUser;

    @NotBlank
    @Column(name = "chat_user_ip")
    private String chatUserIp;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Convert(converter = SessionStatus.Converter.class)
    @Column(name = "status")
    private SessionStatus status;

    public ChatSession() {
        this.startTime = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
    }

    public ChatSession(ChatUser chatUser, String chatUserIp) {
        this.chatUser = chatUser;
        this.chatUserIp = chatUserIp;
        this.startTime = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
    }

    public ChatUser getChatUser() {
        return chatUser;
    }

    public void setChatUser(ChatUser chatUser) {
        this.chatUser = chatUser;
    }

    public String getChatUserIp() {
        return chatUserIp;
    }

    public void setChatUserIp(String chatUserIp) {
        this.chatUserIp = chatUserIp;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
