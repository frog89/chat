package com.franka.chat.data.model;

import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.entity.ChatSession;

import java.util.List;

public class ChatBroadcastInfo {
  private ChatSession fromSession;
  private Chat chat;
  private List<ChatMessage> messages;

  public ChatBroadcastInfo(ChatSession fromSession, Chat chat, List<ChatMessage> messages) {
    this.fromSession = fromSession;
    this.chat = chat;
    this.messages = messages;
  }

  public ChatSession getFromSession() {
    return fromSession;
  }

  public Chat getChat() {
    return this.chat;
  }

  public List<ChatMessage> getMessages() {
    return messages;
  }
}
