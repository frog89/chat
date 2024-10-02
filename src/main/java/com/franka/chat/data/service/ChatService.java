package com.franka.chat.data.service;

import com.franka.chat.data.ChatSessionAttribute;
import com.franka.chat.data.entity.Chat;
import com.franka.chat.data.entity.ChatMessage;
import com.franka.chat.data.entity.ChatSession;
import com.franka.chat.data.entity.ChatUserKind;
import com.franka.chat.data.entity.ChatUserRole;
import com.franka.chat.data.repository.ChatMessageRepository;
import com.franka.chat.data.repository.ChatRepository;
import com.franka.chat.data.repository.ChatSessionRepository;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChatService {
  @Autowired
  private AuthenticationContext authenticationContext;

  @Autowired
  private ChatSessionRepository sessionRepository;
  @Autowired
  private ChatMessageRepository messageRepository;
  @Autowired
  private ChatRepository chatRepository;

  @Autowired
  private HttpServletRequest servletRequest;

  public List<ChatMessage> findChatMessagesByChatId(Long chatId) {
    return messageRepository.findByChatId(chatId);
  }

  public ChatMessage saveChatMessage(ChatMessage msg) {
    return messageRepository.save(msg);
  }

  public List<ChatSession> findAllSessions(String userName) {
    if (userName == null || userName.isEmpty()) {
      return sessionRepository.findAll();
    } else {
      return sessionRepository.searchUserNameIgnoreCase(userName);
    }
  }

  @PostConstruct
  public void generateUsers() {
    if (VaadinSession.getCurrent() != null) {
      VaadinSession.getCurrent().getSession().invalidate();
    }

    String ipAddress = "0:0:0:0:0:0:0:1";
    ChatSession frank = new ChatSession("Frank", ChatUserKind.MALE, ipAddress);
    frank.setUserRole(ChatUserRole.ADMIN);
    ChatSession klaudia = new ChatSession("Klaudia", ChatUserKind.FEMALE, ipAddress);
    ChatSession jan = new ChatSession("Jan", ChatUserKind.MALE, ipAddress);
    ChatSession svenja = new ChatSession("Svenja", ChatUserKind.FEMALE, ipAddress);
    ChatSession nick = new ChatSession("Nick", ChatUserKind.UNKNOWN, ipAddress);

    List<ChatSession> sessions = Stream.of(frank, klaudia, jan, svenja, nick).collect(Collectors.toList());
    if (sessionRepository.count() == 0) {
      sessionRepository.saveAll(sessions);
    }
  }

  public ChatSession getCurrentSession() {
    return (ChatSession)VaadinSession.getCurrent().getAttribute(ChatSessionAttribute.CURRENT_CHAT_SESSION.name());
  }

  public List<Chat> findAllChats() {
    return chatRepository.findAll();
  }

  public Optional<Chat> findChatById(Long chatId) {
    return chatRepository.findById(chatId);
  }

  public List<Chat> findChatBySessionIds(Long firstSessionId, Long secondSessionId) {
    return chatRepository.findChatBySessionIds(firstSessionId, secondSessionId);
  }

  public List<Chat> findChatsBySessionId(Long sessionId) {
    return chatRepository.findChatsBySessionId(sessionId);
  }

  public void saveChat(Chat chat) {
    chatRepository.save(chat);
  }

  public void deleteChat(Chat chat) {
    chatRepository.delete(chat);
  }


    /*
    public void generateSessions() {
        List<ChatUser> users = userRepository.findAll();
        String ipAddress = VaadinSession.getCurrent().getBrowser().getAddress();
        if (userRepository.count() == 0) {
            userRepository.saveAll(users);
            for(ChatUser user : users) {
                ChatSession session = new ChatSession(user, ipAddress);
                sessionRepository.save(session);
            }
        }
        List<ChatSession> sessions = sessionRepository.findAll();

        if (messageRepository.count() == 0) {
            messageRepository.saveAll(
                    Stream.of("Frank:Klaudia:Hallo, Schatz!",
                            "Klaudia:Frank:Hallo",
                            "Jan:Klaudia:Was gibt es?",
                            "Svenja:Jan:Muss weg!",
                            "Nick:Svenja:Ich hab dich auf Insta gesehen!").map(name -> {
                        ChatMessage msg = new ChatMessage();
                        String[] chatToken = name.split(":");
                        String chatTokenFromUser = chatToken[0];
                        String chatTokenToUser = chatToken[1];
                        String chatTokenMsg = chatToken[2];

                        ChatUser fromUser = users.stream()
                                .filter(u -> chatTokenFromUser.equals(u.getName()))
                                .findAny()
                                .orElse(null);
                        ChatSession fromSession =sessions.stream()
                                .filter(s -> fromUser.getId().equals(s.getChatUser().getId()))
                                .findAny()
                                .orElse(null);

                        ChatUser toUser = users.stream()
                                .filter(u -> chatTokenToUser.equals(u.getName()))
                                .findAny()
                                .orElse(null);
                        ChatSession toSession = sessions.stream()
                                .filter(s -> toUser.getId().equals(s.getChatUser().getId()))
                                .findAny()
                                .orElse(null);

                        msg.setFromChatUser(fromUser);
                        msg.setFromChatUserSessionId(fromSession.getId());
                        msg.setToChatUser(toUser);
                        msg.setToChatUserSessionId(toSession.getId());
                        msg.setMessage(chatTokenMsg);
                        return msg;
                    }).collect(Collectors.toList())
            );
        }
    }
    */
}
