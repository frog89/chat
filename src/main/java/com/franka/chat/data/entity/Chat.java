package com.franka.chat.data.entity;

import com.franka.chat.data.AbstractEntity;
import com.franka.chat.util.NotificationUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
public class Chat extends AbstractEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name="first_chat_session_id")
    private ChatSession firstChatSession;

    @ManyToOne(optional = false)
    @JoinColumn(name="second_chat_session_id")
    private ChatSession secondChatSession;

    @Convert(converter = ChatHidden.Converter.class)
    @Column(name = "is_hidden_for_first_chat_session")
    private ChatHidden isHiddenForFirstChatSession;

    @Convert(converter = ChatHidden.Converter.class)
    @Column(name = "is_hidden_for_second_chat_session")
    private ChatHidden isHiddenForSecondChatSession;

    @Column(name = "last_seen_message_id")
    private Long lastSeenMessageId;

    @NotNull
    @Column(name = "start_datetime")
    private LocalDateTime startTime;

    public Chat() {
        this.startTime = LocalDateTime.now();
        lastSeenMessageId = 0L;
        isHiddenForFirstChatSession = ChatHidden.N;
        isHiddenForSecondChatSession = ChatHidden.N;
    }

    public Chat(ChatSession firstSession, ChatSession secondSession) {
        this();
        this.firstChatSession = firstSession;
        this.secondChatSession = secondSession;
    }

    public boolean getIsHidden(ChatSession currentSession) {
        boolean isHidden = this.isHiddenForFirstChatSession == ChatHidden.Y && currentSession.getId().equals(this.firstChatSession.getId());
        if (!isHidden) {
            isHidden = this.isHiddenForSecondChatSession == ChatHidden.Y && currentSession.getId().equals(this.secondChatSession.getId());
        }
        return isHidden;
    }
    public void setIsHidden(ChatSession currentSession, boolean isHidden) {
        if (currentSession.getId().equals(this.firstChatSession.getId())) {
            isHiddenForFirstChatSession = isHidden ? ChatHidden.Y : ChatHidden.N;
            return;
        }
        if (currentSession.getId().equals(this.secondChatSession.getId())) {
            isHiddenForSecondChatSession = isHidden ? ChatHidden.Y : ChatHidden.N;
            return;
        }
        NotificationUtil.showClosableError("setIsHidden: Unknown session given!");
    }

    public boolean hasCurrentSession(ChatSession currentSession) {
        if (this.firstChatSession.getId().equals(currentSession.getId())) {
            return true;
        } else if (this.secondChatSession.getId().equals(currentSession.getId())) {
            return true;
        }
        return false;
    }

    public ChatSession getOtherSession(ChatSession currentSession) {
        if (this.firstChatSession.getId().equals(currentSession.getId())) {
            return this.secondChatSession;
        } else if (this.secondChatSession.getId().equals(currentSession.getId())) {
            return this.firstChatSession;
        }
        return null;
    }

    public ChatSession getFirstChatSession() {
        return firstChatSession;
    }

    public void setFirstChatSession(ChatSession firstChatSession) {
        this.firstChatSession = firstChatSession;
    }

    public ChatSession getSecondChatSession() {
        return secondChatSession;
    }

    public void setSecondChatSession(ChatSession secondChatSession) {
        this.secondChatSession = secondChatSession;
    }

    public ChatHidden getIsHiddenForFirstChatSession() {
        return isHiddenForFirstChatSession;
    }

    public void setIsHiddenForFirstChatSession(ChatHidden isHiddenForFirstChatSession) {
        this.isHiddenForFirstChatSession = isHiddenForFirstChatSession;
    }

    public ChatHidden getIsHiddenForSecondChatSession() {
        return isHiddenForSecondChatSession;
    }

    public void setIsHiddenForSecondChatSession(ChatHidden isHiddenForSecondChatSession) {
        this.isHiddenForSecondChatSession = isHiddenForSecondChatSession;
    }

    public Long getLastSeenMessageId() {
        return lastSeenMessageId;
    }

    public void setLastSeenMessageId(Long lastSeenMessageId) {
        this.lastSeenMessageId = lastSeenMessageId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
