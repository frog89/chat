package com.franka.chat.data.entity;

import com.franka.chat.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name="chat_message")
public class ChatMessage extends AbstractEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name="from_chat_user_id")
    private ChatUser fromChatUser;

    @Min(1)
    private Long fromChatUserSessionId;

    @ManyToOne(optional = false)
    @JoinColumn(name="to_chat_user_id")
    private ChatUser toChatUser;

    @Min(1)
    private Long toChatUserSessionId;

    @NotNull
    private LocalDateTime datetime;

    @NotBlank
    private String message;

    public ChatMessage() {
        this.datetime = LocalDateTime.now();
    }

    public ChatUser getFromChatUser() {
        return fromChatUser;
    }

    public void setFromChatUser(ChatUser fromChatUser) {
        this.fromChatUser = fromChatUser;
    }

    public Long getFromChatUserSessionId() {
        return fromChatUserSessionId;
    }

    public void setFromChatUserSessionId(Long fromChatUserSessionId) {
        this.fromChatUserSessionId = fromChatUserSessionId;
    }

    public ChatUser getToChatUser() {
        return toChatUser;
    }

    public void setToChatUser(ChatUser toChatUser) {
        this.toChatUser = toChatUser;
    }

    public Long getToChatUserSessionId() {
        return toChatUserSessionId;
    }

    public void setToChatUserSessionId(Long toChatUserSessionId) {
        this.toChatUserSessionId = toChatUserSessionId;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
