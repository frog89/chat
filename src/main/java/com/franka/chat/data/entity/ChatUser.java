package com.franka.chat.data.entity;

import com.franka.chat.data.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="chat_user")
public class ChatUser extends AbstractEntity {
    @NotBlank
    private String name;

    @Convert(converter = ChatUserKind.Converter.class)
    @Column(name = "chat_user_kind")
    private ChatUserKind kind;

    @Convert(converter = ChatUserRole.Converter.class)
    @Column(name = "chat_user_role")
    private ChatUserRole role;

    public ChatUser() {
    }

    public ChatUser(String name, ChatUserKind kind) {
        this.name = name;
        this.kind = kind;
        this.role = ChatUserRole.USER;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatUserKind getKind() {
        return kind;
    }

    public void setKind(ChatUserKind kind) {
        this.kind = kind;
    }

    public ChatUserRole getRole() {
        return role;
    }

    public void setRole(ChatUserRole role) {
        this.role = role;
    }
}
