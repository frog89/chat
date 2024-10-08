package com.franka.chat.data.entity;

import com.franka.chat.data.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session")
public class ChatSession extends AbstractEntity {
    @NotBlank
    @Column(name = "chat_user_name")
    private String userName;

    @Convert(converter = ChatUserKind.Converter.class)
    @Column(name = "chat_user_kind")
    private ChatUserKind userKind;

    @Convert(converter = ChatUserRole.Converter.class)
    @Column(name = "chat_user_role")
    private ChatUserRole userRole;

    @NotBlank
    @Column(name = "pwd_hash")
    private String pwdHash;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Convert(converter = SessionStatus.Converter.class)
    @Column(name = "status")
    private SessionStatus status;

    public ChatSession() throws NoSuchAlgorithmException, InvalidKeySpecException {
        this(null, ChatUserKind.UNKNOWN, null);
    }

    public ChatSession(String userName, ChatUserKind userKind, String passwordHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.userName = userName;
        this.userKind = userKind;
        this.userRole = ChatUserRole.USER;
        this.pwdHash = passwordHash;
        this.startTime = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ChatUserKind getUserKind() {
        return userKind;
    }

    public void setUserKind(ChatUserKind userKind) {
        this.userKind = userKind;
    }

    public ChatUserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(ChatUserRole userRole) {
        this.userRole = userRole;
    }

    public @NotBlank String getPwdHash() {
        return pwdHash;
    }

    public void setPwdHash(@NotBlank String pwdHash) {
        this.pwdHash = pwdHash;
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
