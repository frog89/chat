package com.franka.chat.data.entity;

public enum ChatUserKind {
    UNKNOWN, MALE, FEMALE, COUPLE;

    public static String getClassname(ChatUserKind kind) {
        if (kind == ChatUserKind.MALE) {
            return "user-male";
        } else if (kind == ChatUserKind.FEMALE) {
            return "user-female";
        } else if (kind == ChatUserKind.COUPLE) {
            return "user-couple";
        }
        return "user-unknown";
    }

    public static class Converter extends AbstractEnumToStringConverter<ChatUserKind, String> {
        public Converter() {
            super(ChatUserKind.class);
        }
    }
}
