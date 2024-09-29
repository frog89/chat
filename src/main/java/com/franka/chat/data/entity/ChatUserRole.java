package com.franka.chat.data.entity;

public enum ChatUserRole {
  USER, ADMIN;

  public static class Converter extends AbstractEnumToStringConverter<ChatUserRole, String> {
    public Converter() {
      super(ChatUserRole.class);
    }
  }
}
