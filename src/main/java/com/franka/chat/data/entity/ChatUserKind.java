package com.franka.chat.data.entity;

public enum ChatUserKind {
    UNKNOWN, MALE, FEMALE, COUPLE;

    public static String getColor(ChatUserKind kind) {
        if (kind == ChatUserKind.MALE) {
            return "blue";
        } else if (kind == ChatUserKind.FEMALE) {
            return "red";
        } else if (kind == ChatUserKind.COUPLE) {
            return "green";
        }
        return "orange";
    }

    public static String getClassname(ChatUserKind kind) {
        if (kind == ChatUserKind.MALE) {
            return "user_male";
        } else if (kind == ChatUserKind.FEMALE) {
            return "user_female";
        } else if (kind == ChatUserKind.COUPLE) {
            return "user_couple";
        }
        return "user_unknown";
    }

    public static class Converter extends AbstractEnumToStringConverter<ChatUserKind, String> {
        public Converter() {
            super(ChatUserKind.class);
        }
    }
}
