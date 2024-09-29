package com.franka.chat.data.entity;

public enum SessionStatus {
    ACTIVE, ENDED_LOGOUT, ENDED_TIMEOUT;

    public static class Converter extends AbstractEnumToStringConverter<SessionStatus, String> {
        public Converter() {
            super(SessionStatus.class);
        }
    }
}
