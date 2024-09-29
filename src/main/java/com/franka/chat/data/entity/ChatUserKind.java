package com.franka.chat.data.entity;

public enum ChatUserKind {
    UNKNOWN, MALE, FEMALE, COUPLE;

    public static class Converter extends AbstractEnumToStringConverter<ChatUserKind, String> {
        public Converter() {
            super(ChatUserKind.class);
        }
    }
}
