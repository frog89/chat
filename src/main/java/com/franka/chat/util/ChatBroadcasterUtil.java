package com.franka.chat.util;

import com.franka.chat.data.model.ChatBroadcastInfo;
import com.vaadin.flow.shared.Registration;
import org.atmosphere.cpr.Broadcaster;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ChatBroadcasterUtil {
  static Executor executor = Executors.newSingleThreadExecutor();

  static LinkedList<Consumer<ChatBroadcastInfo>> listeners = new LinkedList<>();

  public static synchronized Registration register(Consumer<ChatBroadcastInfo> listener) {
    listeners.add(listener);

    return () -> {
      synchronized (Broadcaster.class) {
        listeners.remove(listener);
      }
    };
  }

  public static synchronized void broadcast(ChatBroadcastInfo info) {
    for (Consumer<ChatBroadcastInfo> listener : listeners) {
      executor.execute(() -> listener.accept(info));
    }
  }
}