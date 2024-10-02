package com.franka.chat.data.entity;

import com.franka.chat.data.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name="chat_message")
public class ChatMessage extends AbstractEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name="chat_id")
    private Chat chat;

    @ManyToOne(optional = false)
    @JoinColumn(name="from_chat_session_id")
    private ChatSession fromSession;

    @NotNull
    private LocalDateTime datetime;

    @NotBlank
    private String message;

    public ChatMessage() {
        this.datetime = LocalDateTime.now();
    }

    public ChatMessage(Chat chat, ChatSession fromSession, String message) {
        this.chat = chat;
        this.fromSession = fromSession;
        this.message = message;
        this.datetime = LocalDateTime.now();
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public ChatSession getFromSession() {
        return fromSession;
    }

    public void setFromSession(ChatSession fromSession) {
        this.fromSession = fromSession;
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
