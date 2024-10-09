package com.franka.chat.data.entity;

public enum ChatHidden {
    Y, N;

    public static class Converter extends AbstractEnumToStringConverter<ChatHidden, String> {
        public Converter() {
            super(ChatHidden.class);
        }
    }
}
